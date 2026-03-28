package com.prakhar.coretrading.controller;

import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.util.LogUtil;
import com.prakhar.coretrading.dto.CreateWalletRequest;
import com.prakhar.coretrading.service.CoreTradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private static final Logger log = LoggerFactory.getLogger(InternalController.class);
    private final CoreTradingService coreTradingService;

    public InternalController(CoreTradingService coreTradingService) {
        this.coreTradingService = coreTradingService;
    }

    @PostMapping("/wallet/create")
    public ResponseEntity<ApiResponse<WalletDTO>> createWallet(
            @RequestBody CreateWalletRequest request,
            @RequestHeader("X-Internal-Api-Key") String apiKey) {

        log.info(LogUtil.info(
            "core-trading-service",
            "POST /internal/wallet/create",
            request.getUserId().toString(),
            "Creating wallet for new user: " + LogUtil.maskEmail(request.getEmail())
        ));

        WalletDTO wallet = coreTradingService.createWalletForUser(
                request.getUserId(),
                request.getEmail(),
                request.getFullName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wallet created successfully", wallet));
    }

    @PostMapping("/wallet/credit-bonus/{userId}")
    public ResponseEntity<ApiResponse<WalletDTO>> creditSignupBonus(
            @PathVariable Long userId,
            @RequestHeader("X-Internal-Api-Key") String apiKey) {

        WalletDTO wallet = coreTradingService.creditSignupBonus(userId);
        return ResponseEntity.ok(ApiResponse.success("Bonus credited", wallet));
    }
}
