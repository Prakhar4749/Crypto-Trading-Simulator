package com.prakhar.auth.feign;

import com.prakhar.auth.dto.request.CreateWalletRequest;
import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.exception.ExternalServiceException;
import org.springframework.stereotype.Component;

@Component
public class CoreTradingClientFallback implements CoreTradingClient {
    
    @Override
    public ApiResponse<WalletDTO> createWalletForUser(CreateWalletRequest request, String internalApiKey) {
        throw new ExternalServiceException(
            "core-trading-service",
            "Wallet service is currently unavailable. Please try signing up again."
        );
    }

    @Override
    public ApiResponse<WalletDTO> creditSignupBonus(Long userId, String internalApiKey) {
        throw new ExternalServiceException(
            "core-trading-service",
            "Could not credit bonus. Please try again."
        );
    }
}
