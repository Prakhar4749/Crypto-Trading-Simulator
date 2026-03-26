package com.prakhar.coretrading.controller;

import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.PaymentDetailsDTO;
import com.prakhar.coretrading.service.CoreTradingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-details")
public class PaymentDetailsController {

    private final CoreTradingService service;

    public PaymentDetailsController(CoreTradingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDetailsDTO>> addPaymentDetails(
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody PaymentDetailsDTO request) throws Exception {
        PaymentDetailsDTO response = service.addPaymentDetails(userId, request);
        return new ResponseEntity<>(ApiResponse.success("Payment details added successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<PaymentDetailsDTO>> getUserPaymentDetails(
            @RequestHeader("X-User-ID") Long userId) throws Exception {
        PaymentDetailsDTO response = service.getUserPaymentDetails(userId);
        return ResponseEntity.ok(ApiResponse.success("Payment details fetched successfully", response));
    }
}
