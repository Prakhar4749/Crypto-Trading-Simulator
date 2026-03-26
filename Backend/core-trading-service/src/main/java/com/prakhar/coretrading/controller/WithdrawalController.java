package com.prakhar.coretrading.controller;

import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.WithdrawalDTO;
import com.prakhar.coretrading.service.WithdrawalService;
import com.prakhar.common.util.RoleValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api")
@Validated
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/withdrawal/{amount}")
    public ResponseEntity<ApiResponse<WithdrawalDTO>> requestWithdrawal(
            @PathVariable @NotNull @DecimalMin("0.01") BigDecimal amount,
            @RequestHeader("X-User-ID") Long userId) {
        WithdrawalDTO withdrawal = withdrawalService.requestWithdrawal(userId, amount);
        return new ResponseEntity<>(ApiResponse.success("Withdrawal requested successfully", withdrawal), HttpStatus.OK);
    }

    @PatchMapping("/admin/withdrawal/{id}/proceed/{accept}")
    public ResponseEntity<ApiResponse<WithdrawalDTO>> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("X-User-Role") String role) throws Exception {
        RoleValidator.requireAdmin(role);
        WithdrawalDTO withdrawal = withdrawalService.processWithdrawal(id, accept);
        return new ResponseEntity<>(ApiResponse.success("Withdrawal processed successfully", withdrawal), HttpStatus.OK);
    }

    @GetMapping("/withdrawal")
    public ResponseEntity<ApiResponse<List<WithdrawalDTO>>> getWithdrawalHistory(
            @RequestHeader("X-User-ID") Long userId) {
        List<WithdrawalDTO> history = withdrawalService.getUsersWithdrawalHistory(userId);
        return new ResponseEntity<>(ApiResponse.success("Withdrawal history fetched successfully", history), HttpStatus.OK);
    }

    @GetMapping("/admin/withdrawal")
    public ResponseEntity<ApiResponse<List<WithdrawalDTO>>> getAllWithdrawalRequests(
            @RequestHeader("X-User-Role") String role) {
        RoleValidator.requireAdmin(role);
        List<WithdrawalDTO> requests = withdrawalService.getAllWithdrawalRequests();
        return new ResponseEntity<>(ApiResponse.success("All withdrawal requests fetched successfully", requests), HttpStatus.OK);
    }
}
