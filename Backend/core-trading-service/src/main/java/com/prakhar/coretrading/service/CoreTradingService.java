package com.prakhar.coretrading.service;

import com.prakhar.common.dto.PaymentDetailsDTO;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.dto.WalletTransactionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CoreTradingService {
    void processTrade(Long userId, String coinId, double quantity, String orderType) throws Exception;
    void handleRazorpayPayment(Long userId, Long amountInInr);
    
    // Wallet Transfer
    WalletDTO transferToWallet(Long senderUserId, Long receiverWalletId, BigDecimal amount, String purpose) throws Exception;
    List<WalletTransactionDTO> getTransactionHistory(Long userId) throws Exception;

    // Payment Details
    PaymentDetailsDTO addPaymentDetails(Long userId, PaymentDetailsDTO paymentDetails) throws Exception;
    PaymentDetailsDTO getUserPaymentDetails(Long userId) throws Exception;
}
