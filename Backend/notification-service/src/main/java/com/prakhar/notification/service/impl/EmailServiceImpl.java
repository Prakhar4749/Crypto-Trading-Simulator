package com.prakhar.notification.service.impl;

import com.prakhar.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    @Value("${notification.otp-expiry}")
    private int otpExpiry;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            logger.info("Email sent to: {}, Subject: {}", maskEmail(to), subject);
        } catch (MessagingException e) {
            logger.error("Failed to send email to: {}, Error: {}", maskEmail(to), e.getMessage());
        }
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp, String purpose) {
        String subject = "Verification Code - " + purpose;
        String content = "<h3>Your OTP for " + purpose + " is: " + otp + "</h3>" +
                         "<p>This code will expire in " + otpExpiry + " minutes.</p>";
        sendEmail(toEmail, subject, content);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String userName) {
        String subject = "Welcome to Crypto Trading Simulator!";
        String content = "<h1>Welcome " + userName + "!</h1>" +
                         "<p>Your account has been created successfully. You can now start trading with your virtual balance.</p>";
        sendEmail(toEmail, subject, content);
    }

    @Override
    public void sendTradeConfirmationEmail(String toEmail, String coinName, BigDecimal amount, String type) {
        String subject = "Trade Confirmation: " + type;
        String content = "<h3>Trade Executed Successfully</h3>" +
                         "<p>Type: " + type + "</p>" +
                         "<p>Coin: " + coinName + "</p>" +
                         "<p>Total Amount: $" + amount + "</p>";
        sendEmail(toEmail, subject, content);
    }

    @Override
    public void sendWithdrawalStatusEmail(String toEmail, String status, BigDecimal amount) {
        String subject = "Withdrawal Update - " + status;
        String content = "<h3>Withdrawal Request " + status + "</h3>" +
                         "<p>Amount: $" + amount + "</p>" +
                         "<p>Status: " + status + "</p>";
        sendEmail(toEmail, subject, content);
    }

    private String maskEmail(String email) {
        if (email == null || email.length() < 4) return "****";
        return email.substring(0, 3) + "***" + email.substring(email.indexOf("@"));
    }
}
