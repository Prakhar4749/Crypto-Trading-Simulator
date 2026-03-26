package com.prakhar.coretrading.controller;

import com.prakhar.common.dto.*;
import com.prakhar.coretrading.dto.request.TradeRequest;
import com.prakhar.coretrading.dto.request.TransferRequest;
import com.prakhar.coretrading.entity.Wallet;
import com.prakhar.coretrading.entity.WatchlistCoin;
import com.prakhar.coretrading.mapper.TradingMapper;
import com.prakhar.coretrading.repository.*;
import com.prakhar.coretrading.service.CoreTradingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CoreTradingController {

    private final CoreTradingService service;
    private final WalletRepository walletRepository;
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final WatchlistCoinRepository watchlistCoinRepository;
    private final TradingMapper mapper;

    public CoreTradingController(CoreTradingService service, 
                                 WalletRepository walletRepository,
                                 OrderRepository orderRepository,
                                 AssetRepository assetRepository,
                                 WatchlistCoinRepository watchlistCoinRepository,
                                 TradingMapper mapper) {
        this.service = service;
        this.walletRepository = walletRepository;
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.watchlistCoinRepository = watchlistCoinRepository;
        this.mapper = mapper;
    }

    @PostMapping("/orders/pay")
    public ResponseEntity<ApiResponse<Void>> processTrade(@RequestHeader("X-User-ID") Long userId, @Valid @RequestBody TradeRequest request) throws Exception {
        service.processTrade(
                userId,
                request.getCoinId(),
                request.getQuantity(),
                request.getOrderType()
        );
        return ResponseEntity.ok(ApiResponse.success("Trade processed successfully", null));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrderHistory(@RequestHeader("X-User-ID") Long userId) {
        List<OrderDTO> orders = orderRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream().map(mapper::toOrderDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Order history fetched successfully", orders));
    }

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<WalletDTO>> getWallet(@RequestHeader("X-User-ID") Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        return ResponseEntity.ok(ApiResponse.success("Wallet fetched successfully", mapper.toWalletDTO(wallet)));
    }

    @PutMapping("/wallet/transfer")
    public ResponseEntity<ApiResponse<WalletDTO>> walletToWalletTransfer(
            @RequestHeader("X-User-ID") Long senderUserId,
            @Valid @RequestBody TransferRequest request) throws Exception {
        
        WalletDTO wallet = service.transferToWallet(senderUserId, request.getReceiverWalletId(), request.getAmount(), request.getPurpose());
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", wallet));
    }

    @GetMapping("/wallet/transactions")
    public ResponseEntity<ApiResponse<List<WalletTransactionDTO>>> getWalletTransactions(@RequestHeader("X-User-ID") Long userId) throws Exception {
        return ResponseEntity.ok(ApiResponse.success("Transactions fetched successfully", service.getTransactionHistory(userId)));
    }

    @GetMapping("/assets")
    public ResponseEntity<ApiResponse<List<AssetDTO>>> getAssets(@RequestHeader("X-User-ID") Long userId) {
        List<AssetDTO> assets = assetRepository.findByUserId(userId)
                .stream().map(mapper::toAssetDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Assets fetched successfully", assets));
    }

    @GetMapping("/assets/coin/{coinId}")
    public ResponseEntity<ApiResponse<AssetDTO>> getAsset(@RequestHeader("X-User-ID") Long userId, @PathVariable String coinId) {
        AssetDTO asset = assetRepository.findByUserIdAndCoinId(userId, coinId)
                .map(mapper::toAssetDTO).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Asset fetched successfully", asset));
    }

    @GetMapping("/watchlist/user")
    public ResponseEntity<ApiResponse<List<String>>> getWatchlist(@RequestHeader("X-User-ID") Long userId) {
        List<String> coinIds = watchlistCoinRepository.findByUserId(userId)
                .stream().map(WatchlistCoin::getCoinId).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Watchlist fetched successfully", coinIds));
    }

    @PatchMapping("/watchlist/add/coin/{coinId}")
    public ResponseEntity<ApiResponse<Void>> toggleWatchlist(@RequestHeader("X-User-ID") Long userId, @PathVariable String coinId) {
        Optional<WatchlistCoin> existing = watchlistCoinRepository.findByUserIdAndCoinId(userId, coinId);
        if (existing.isPresent()) {
            watchlistCoinRepository.delete(existing.get());
        } else {
            WatchlistCoin watchlistCoin = new WatchlistCoin();
            watchlistCoin.setUserId(userId);
            watchlistCoin.setCoinId(coinId);
            watchlistCoinRepository.save(watchlistCoin);
        }
        return ResponseEntity.ok(ApiResponse.success("Watchlist updated successfully", null));
    }

    @PostMapping("/wallet/webhook/razorpay")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        Map<String, Object> payloadMap = (Map<String, Object>) payload.get("payload");
        if (payloadMap != null) {
            Map<String, Object> payment = (Map<String, Object>) ((Map<String, Object>) payloadMap.get("payment")).get("entity");
            Long userId = Long.valueOf(String.valueOf(((Map<String, Object>) payment.get("notes")).get("user_id")));
            Long amountInInr = ((Number) payment.get("amount")).longValue() / 100;
            service.handleRazorpayPayment(userId, amountInInr);
        }
        return ResponseEntity.ok().build();
    }
}
