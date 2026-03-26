package com.prakhar.coretrading.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.prakhar.common.dto.*;
import com.prakhar.common.enums.WalletTransactionType;
import com.prakhar.common.event.TradeExecutedEvent;
import com.prakhar.common.exception.*;
import com.prakhar.coretrading.utils.RequestContext;
import com.prakhar.coretrading.entity.*;
import com.prakhar.coretrading.feign.MarketAiClient;
import com.prakhar.coretrading.config.RazorpayConfig;
import com.prakhar.coretrading.mapper.TradingMapper;
import com.prakhar.coretrading.repository.*;
import com.prakhar.coretrading.service.CoreTradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final RazorpayConfig razorpayConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TradingMapper mapper;

    public CoreTradingServiceImpl(WalletRepository walletRepository, 
                                  WalletTransactionRepository walletTransactionRepository,
                                  AssetRepository assetRepository,
                                  OrderRepository orderRepository,
                                  PaymentDetailsRepository paymentDetailsRepository,
                                  MarketAiClient marketClient, 
                                  RazorpayConfig razorpayConfig, 
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  TradingMapper mapper) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.assetRepository = assetRepository;
        this.orderRepository = orderRepository;
        this.paymentDetailsRepository = paymentDetailsRepository;
        this.marketClient = marketClient;
        this.razorpayConfig = razorpayConfig;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void processTrade(Long userId, String coinId, double quantity, String orderType) {
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }

        JsonNode coinDetails;
        try {
            coinDetails = marketClient.getCoinDetails(coinId);
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

        Order order = new Order();
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
                userId, RequestContext.getUserEmail(), coinId, quantity, totalCost, orderType, "SUCCESS", LocalDateTime.now()
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
}
