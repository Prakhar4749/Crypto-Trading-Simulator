package com.prakhar.auth.feign;

import com.prakhar.auth.dto.request.CreateWalletRequest;
import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.WalletDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "core-trading-service",
    fallback = CoreTradingClientFallback.class
)
public interface CoreTradingClient {

    @PostMapping("/internal/wallet/create")
    ApiResponse<WalletDTO> createWalletForUser(
        @RequestBody CreateWalletRequest request
    );

    @PostMapping("/internal/wallet/credit-bonus/{userId}")
    ApiResponse<WalletDTO> creditSignupBonus(
        @PathVariable("userId") Long userId
    );
}
