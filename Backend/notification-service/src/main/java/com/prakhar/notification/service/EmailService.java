package com.prakhar.notification.service;

import com.prakhar.common.event.TradeExecutedEvent;
import com.prakhar.common.event.WithdrawalStatusEvent;
import java.math.BigDecimal;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendOtpEmail(String toEmail, String fullName, String otp, String purpose);
    void sendWelcomeEmail(String toEmail, String fullName, String bonusToken, boolean sendBonusLink);
    void sendClaimBonusEmail(String toEmail, String fullName, String claimToken);
    void sendTradeConfirmationEmail(String toEmail, String fullName, TradeExecutedEvent event);
    void sendWithdrawalStatusEmail(String toEmail, String fullName, WithdrawalStatusEvent event);
}
