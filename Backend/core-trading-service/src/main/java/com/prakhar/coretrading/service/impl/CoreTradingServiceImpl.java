package com.prakhar.coretrading.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.prakhar.common.dto.*;
import com.prakhar.common.enums.WalletTransactionType;
import com.prakhar.common.event.TradeExecutedEvent;
import com.prakhar.common.exception.*;
import com.prakhar.common.util.LogUtil;
import com.prakhar.coretrading.utils.RequestContext;
import com.prakhar.coretrading.entity.*;
import com.prakhar.coretrading.feign.AuthClient;
import com.prakhar.coretrading.feign.MarketAiClient;
import com.prakhar.coretrading.config.RazorpayConfig;
import com.prakhar.coretrading.mapper.TradingMapper;
import com.prakhar.coretrading.repository.*;
import com.prakhar.coretrading.service.CoreTradingService;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoreTradingServiceImpl implements CoreTradingService {

    private static final Logger log = LoggerFactory.getLogger(CoreTradingServiceImpl.class);

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final AssetRepository assetRepository;
    private final OrderRepository orderRepository;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final MarketAiClient marketClient;
    private final AuthClient authClient;
    private final RazorpayConfig razorpayConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TradingMapper mapper;

    @Value("${wallet.signup-bonus:10000}")
    private String signupBonusAmount;

    @Value("${internal.service.api.key}")
    private String internalApiKey;

    public CoreTradingServiceImpl(WalletRepository walletRepository, 
                                  WalletTransactionRepository walletTransactionRepository,
                                  AssetRepository assetRepository,
                                  OrderRepository orderRepository,
                                  PaymentDetailsRepository paymentDetailsRepository,
                                  MarketAiClient marketClient, 
                                  AuthClient authClient,
                                  RazorpayConfig razorpayConfig, 
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  TradingMapper mapper) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.assetRepository = assetRepository;
        this.orderRepository = orderRepository;
        this.paymentDetailsRepository = paymentDetailsRepository;
        this.marketClient = marketClient;
        this.authClient = authClient;
        this.razorpayConfig = razorpayConfig;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PaymentOrderResponse createDepositOrder(Long userId, Long amount) throws Exception {
        // 1. Get user KYC status via Feign
        ApiResponse<UserDTO> authResponse = authClient.getUserById(userId, internalApiKey);
        UserDTO user = authResponse.getData();

        if (user == null || !"VERIFIED".equals(user.getKycStatus())) {
            List<String> missing = new ArrayList<>();
            if (user != null) {
                if (!user.isVerified()) missing.add("Email verification");
                if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) missing.add("Phone number");
                if (user.getAddress() == null || user.getAddress().isBlank()) missing.add("Address");
            } else {
                missing.add("Full KYC Profile");
            }

            String missingStr = String.join(", ", missing);
            throw new BusinessException(
                "Account verification required to deposit. Please complete: " + missingStr + 
                ". Go to Profile > Verification."
            );
        }

        // 2. Create Razorpay Order
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayConfig.getKeyId(), razorpayConfig.getKeySecret());

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // amount in the smallest currency unit (paise)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt_" + userId + "_" + System.currentTimeMillis());
            
            JSONObject notes = new JSONObject();
            notes.put("user_id", userId);
            orderRequest.put("notes", notes);

            com.razorpay.Order rzpOrder = razorpay.orders.create(orderRequest);

            return new PaymentOrderResponse(
                rzpOrder.get("id"),
                ((Number) rzpOrder.get("amount")).longValue() / 100,
                rzpOrder.get("currency"),
                rzpOrder.get("status")
            );
        } catch (Exception e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new ExternalServiceException("razorpay", "Could not initiate payment. Please try again later.");
        }
    }

    @Override
    @Transactional
    public void processTrade(Long userId, String coinId, double quantity, String orderType) {
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }

        JsonNode coinDetails;
        try {
            ApiResponse<JsonNode> response = marketClient.getCoinDetails(coinId);
            coinDetails = response.getData();
        } catch (Exception e) {
            throw new ExternalServiceException("market-ai-service", "Could not fetch coin details for " + coinId);
        }

        if (coinDetails == null || coinDetails.get("market_data") == null) {
            throw new ResourceNotFoundException("Coin", coinId);
        }

        double currentPrice = coinDetails.get("market_data").get("current_price").get("usd").asDouble();
        BigDecimal totalCost = BigDecimal.valueOf(currentPrice * quantity);

        double buyPrice = 0;
        double sellPrice = 0;

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId=" + userId));

        if ("BUY".equalsIgnoreCase(orderType)) {
            if (wallet.getBalance().compareTo(totalCost) < 0) {
                throw new InsufficientBalanceException(totalCost, wallet.getBalance());
            }
            wallet.setBalance(wallet.getBalance().subtract(totalCost));
            walletRepository.save(wallet);

            Asset asset = assetRepository.findByUserIdAndCoinId(userId, coinId)
                    .orElse(new Asset(null, userId, coinId, 0.0, 0.0));
            
            asset.setQuantity(asset.getQuantity() + quantity);
            asset.setBuyPrice(currentPrice);
            assetRepository.save(asset);
            
            buyPrice = currentPrice;
        } else {
            Asset asset = assetRepository.findByUserIdAndCoinId(userId, coinId)
                    .orElseThrow(() -> new BusinessException("You don't own any " + coinId + " to sell"));
            
            if (asset.getQuantity() < quantity) {
                throw new BusinessException("Insufficient " + coinId + " holdings. Available: " + asset.getQuantity());
            }
            
            wallet.setBalance(wallet.getBalance().add(totalCost));
            walletRepository.save(wallet);

            asset.setQuantity(asset.getQuantity() - quantity);
            assetRepository.save(asset);
            
            buyPrice = asset.getBuyPrice();
            sellPrice = currentPrice;
        }

        com.prakhar.coretrading.entity.Order order = new com.prakhar.coretrading.entity.Order();
        order.setUserId(userId);
        order.setOrderType(orderType);
        order.setPrice(totalCost);
        order.setTimestamp(LocalDateTime.now());
        order.setStatus("SUCCESS");
        order.setCoinId(coinId);
        order.setQuantity(quantity);
        order.setBuyPrice(buyPrice);
        order.setSellPrice(sellPrice);
        orderRepository.save(order);

        kafkaTemplate.send("trade-executed", new TradeExecutedEvent(
                userId, RequestContext.getUserEmail(), RequestContext.getUserFullName(), coinId, quantity, totalCost, orderType, "SUCCESS", LocalDateTime.now()
        ));
    }

    @Override
    @Transactional
    public void handleRazorpayPayment(Long userId, Long amountInInr) {
        BigDecimal simRupees = BigDecimal.valueOf(amountInInr).multiply(BigDecimal.valueOf(razorpayConfig.getExchangeRate()));
        
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId=" + userId));
        
        wallet.setBalance(wallet.getBalance().add(simRupees));
        walletRepository.save(wallet);
        
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType(WalletTransactionType.ADD_MONEY);
        transaction.setDate(LocalDateTime.now());
        transaction.setPurpose("Razorpay Deposit");
        transaction.setAmount(simRupees);
        walletTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public WalletDTO transferToWallet(Long senderUserId, Long receiverWalletId, BigDecimal amount, String purpose) {
        log.info("Transferring {} from userId {} to walletId {}", amount, senderUserId, receiverWalletId);
        
        Wallet senderWallet = walletRepository.findByUserIdForUpdate(senderUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId=" + senderUserId));
        
        if (senderWallet.getId().equals(receiverWalletId)) {
            throw new BusinessException("Cannot transfer to your own wallet");
        }

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(amount, senderWallet.getBalance());
        }

        Wallet receiverWallet = walletRepository.findById(receiverWalletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "id=" + receiverWalletId));

        // Update balances
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // Record transactions
        WalletTransaction senderTx = new WalletTransaction(null, senderWallet.getId(), WalletTransactionType.WALLET_TRANSFER, 
                LocalDateTime.now(), receiverWallet.getId().toString(), purpose, amount.negate());
        
        WalletTransaction receiverTx = new WalletTransaction(null, receiverWallet.getId(), WalletTransactionType.WALLET_TRANSFER, 
                LocalDateTime.now(), senderWallet.getId().toString(), purpose, amount);

        walletTransactionRepository.save(senderTx);
        walletTransactionRepository.save(receiverTx);

        log.info("Transfer successful");
        return mapper.toWalletDTO(senderWallet);
    }

    @Override
    public List<WalletTransactionDTO> getTransactionHistory(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId=" + userId));
        return walletTransactionRepository.findByWalletIdOrderByDateDesc(wallet.getId())
                .stream().map(mapper::toWalletTransactionDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentDetailsDTO addPaymentDetails(Long userId, PaymentDetailsDTO paymentDetailsDto) {
        if (paymentDetailsRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateResourceException("Payment details already exist for this user");
        }
        
        PaymentDetails details = new PaymentDetails();
        details.setAccountNumber(paymentDetailsDto.getAccountNumber());
        details.setAccountHolderName(paymentDetailsDto.getAccountHolderName());
        details.setIfsc(paymentDetailsDto.getIfsc());
        details.setBankName(paymentDetailsDto.getBankName());
        details.setUserId(userId);
        
        PaymentDetails saved = paymentDetailsRepository.save(details);
        return mapper.toPaymentDetailsDTO(saved);
    }

    @Override
    public PaymentDetailsDTO getUserPaymentDetails(Long userId) {
        PaymentDetails details = paymentDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentDetails", "userId=" + userId));
        return mapper.toPaymentDetailsDTO(details);
    }

    @Override
    @Transactional
    public WalletDTO createWalletForUser(Long userId, String email, String fullName) {
        // Check if wallet already exists
        if (walletRepository.existsByUserId(userId)) {
            log.warn(LogUtil.info(
                "core-trading-service",
                "createWalletForUser",
                userId.toString(),
                "Wallet already exists — skipping"
            ));
            return mapper.toWalletDTO(
                walletRepository.findByUserId(userId).orElseThrow()
            );
        }

        // Create wallet with ZERO balance
        // Bonus credited separately via claim
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        // wallet.setCreatedAt(LocalDateTime.now()); // Add if field exists
        Wallet saved = walletRepository.save(wallet);

        log.info(LogUtil.info(
            "core-trading-service",
            "createWalletForUser",
            userId.toString(),
            "Wallet created | balance=0 | bonus will be credited on claim"
        ));

        return mapper.toWalletDTO(saved);
    }

    @Override
    @Transactional
    public WalletDTO creditSignupBonus(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId=" + userId));

        BigDecimal bonusAmount = new BigDecimal(signupBonusAmount);
        wallet.setBalance(wallet.getBalance().add(bonusAmount));
        walletRepository.save(wallet);

        // Record transaction
        WalletTransaction tx = new WalletTransaction();
        tx.setWalletId(wallet.getId());
        tx.setType(WalletTransactionType.SIGNUP_BONUS);
        tx.setAmount(bonusAmount);
        tx.setPurpose("Welcome bonus from CoinDesk");
        tx.setDate(LocalDateTime.now());
        walletTransactionRepository.save(tx);

        log.info(LogUtil.info(
            "core-trading-service",
            "creditSignupBonus",
            userId.toString(),
            "Bonus credited: $" + bonusAmount
        ));

        return mapper.toWalletDTO(wallet);
    }
}
