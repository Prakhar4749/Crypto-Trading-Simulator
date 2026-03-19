package com.prakhar.notification.service;

import java.math.BigDecimal;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendOtpEmail(String toEmail, String otp, String purpose);
    void sendWelcomeEmail(String toEmail, String userName);
    void sendTradeConfirmationEmail(String toEmail, String coinName, BigDecimal amount, String type);
    void sendWithdrawalStatusEmail(String toEmail, String status, BigDecimal amount);
}
