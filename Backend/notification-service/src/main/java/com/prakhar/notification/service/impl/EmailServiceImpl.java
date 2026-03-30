package com.prakhar.notification.service.impl;

import com.prakhar.common.event.TradeExecutedEvent;
import com.prakhar.common.event.WithdrawalStatusEvent;
import com.prakhar.notification.service.EmailService;
import com.prakhar.notification.util.EmailTemplateBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.PostConstruct;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    @Value("${app.name:CoinDesk}")
    private String appName;

    @Value("${app.logo-url:}")
    private String logoUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${notification.otp-expiry:10}")
    private int otpExpiryMinutes;

    @Value("${admin.email:}")
    private String adminEmail;

    @Value("${app.email.from}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostConstruct
    public void testEmailOnStartup() {
        if (adminEmail == null || adminEmail.isBlank()) {
            logger.warn("Admin email not configured, skipping SMTP startup test");
            return;
        }

        try {
            SimpleMailMessage test = new SimpleMailMessage();
            test.setFrom(fromEmail); // <-- ADD THIS
            test.setTo(adminEmail);
            test.setSubject("[" + appName + "] Notification Service Started");
            test.setText("Notification service is running and email is working correctly.");
            mailSender.send(test);
            logger.info("SMTP test email sent successfully to: {}", maskEmail(adminEmail));
        } catch (Exception e) {
            logger.error("SMTP FAILED on startup: {} — Check SMTP_USERNAME and SMTP_PASSWORD", e.getMessage());
        }
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // <-- ADD THIS (Sets both the email and the display name)
            helper.setFrom(fromEmail, appName);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            logger.info("Email sent successfully to: {}, Subject: {}", maskEmail(to), subject);
        } catch (jakarta.mail.MessagingException | org.springframework.mail.MailException | java.io.UnsupportedEncodingException e) {
            logger.error("FAILED to send email to: {}, Error: {}", maskEmail(to), e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String fullName, String bonusToken, boolean sendBonusLink) {
        String firstName = fullName.split(" ")[0];
        String subject = "🎉 Welcome to " + appName + ", " + firstName + "!";

        StringBuilder content = new StringBuilder();
        content.append(EmailTemplateBuilder.greeting(firstName));
        content.append(EmailTemplateBuilder.para("Welcome to " + appName + "! You're now part of the most exciting crypto trading simulator. Practice trading with virtual currency — no real money at risk!"));

        if (sendBonusLink) {
            content.append(EmailTemplateBuilder.infoBox("Your Welcome Bonus", "$10,000 Virtual USD"));
            content.append(EmailTemplateBuilder.para("Click the button below to claim your welcome bonus. This link expires in 24 hours."));
            content.append(EmailTemplateBuilder.ctaButton(frontendUrl + "/claim-bonus?token=" + bonusToken, "🎁 Claim My $10,000 Bonus", "#ffd700"));
            content.append(EmailTemplateBuilder.warningBox("This bonus can only be claimed once and expires in 24 hours."));
        } else {
            content.append(EmailTemplateBuilder.para("To claim your $10,000 welcome bonus, please verify your email address first. Once verified, we'll send you the bonus claim link."));
            content.append(EmailTemplateBuilder.ctaButton(frontendUrl + "/verify-email", "✉️ Verify Email Now", "#ffd700"));
        }

        String footer = EmailTemplateBuilder.footer(appName, frontendUrl);
        String body = EmailTemplateBuilder.wrap(appName, logoUrl, content.toString(), footer);

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendClaimBonusEmail(String toEmail, String fullName, String claimToken) {
        String subject = "🎁 Your $10,000 " + appName + " Bonus is waiting!";

        StringBuilder content = new StringBuilder();
        content.append(EmailTemplateBuilder.greeting(fullName));
        content.append(EmailTemplateBuilder.para("Your email has been verified! Your $10,000 virtual USD bonus is ready to claim."));
        content.append(EmailTemplateBuilder.infoBox("Bonus Amount", "$10,000 Virtual USD"));
        content.append(EmailTemplateBuilder.infoBox("Expires In", "24 hours"));
        content.append(EmailTemplateBuilder.ctaButton(frontendUrl + "/claim-bonus?token=" + claimToken, "🚀 Claim $10,000 Now", "#ffd700"));
        content.append(EmailTemplateBuilder.divider());
        content.append(EmailTemplateBuilder.warningBox("This is a one-time bonus. The link expires in 24 hours."));

        String footer = EmailTemplateBuilder.footer(appName, frontendUrl);
        String body = EmailTemplateBuilder.wrap(appName, logoUrl, content.toString(), footer);

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendOtpEmail(String toEmail, String fullName, String otp, String purpose) {
        String subject;
        String purposeMessage;

        switch (purpose) {
            case "EMAIL_VERIFY" -> {
                subject = "✉️ Verify your " + appName + " email";
                purposeMessage = "Use the OTP below to verify your email address and complete your registration.";
            }
            case "TWO_FACTOR" -> {
                subject = "🔐 " + appName + " 2FA Code";
                purposeMessage = "A sign-in attempt requires two-factor authentication. Use the code below to proceed.";
            }
            case "FORGOT_PASSWORD" -> {
                subject = "🔑 Reset your " + appName + " password";
                purposeMessage = "You requested a password reset. Use the OTP below to set a new password.";
            }
            default -> {
                subject = "Verification Code - " + appName;
                purposeMessage = "Your verification code is provided below.";
            }
        }

        StringBuilder content = new StringBuilder();
        content.append(EmailTemplateBuilder.greeting(fullName));
        content.append(EmailTemplateBuilder.para(purposeMessage));
        content.append(EmailTemplateBuilder.otpBox(otp));
        content.append(EmailTemplateBuilder.infoBox("Expires In", otpExpiryMinutes + " minutes"));
        content.append(EmailTemplateBuilder.warningBox("Never share this OTP with anyone. " + appName + " will never ask for your OTP."));

        String footer = EmailTemplateBuilder.footer(appName, frontendUrl);
        String body = EmailTemplateBuilder.wrap(appName, logoUrl, content.toString(), footer);

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendTradeConfirmationEmail(String toEmail, String fullName, TradeExecutedEvent event) {
        String subject = "📈 Trade executed: " + event.getOrderType() + " " + event.getCoinId().toUpperCase();

        BigDecimal totalValue = event.getPrice().multiply(BigDecimal.valueOf(event.getQuantity()));

        StringBuilder content = new StringBuilder();
        content.append(EmailTemplateBuilder.greeting(fullName));
        content.append(EmailTemplateBuilder.para("Your trade has been executed successfully on " + appName + "."));
        content.append(EmailTemplateBuilder.infoBox("Order Type", event.getOrderType()));
        content.append(EmailTemplateBuilder.infoBox("Coin", event.getCoinId().toUpperCase()));
        content.append(EmailTemplateBuilder.infoBox("Quantity", String.valueOf(event.getQuantity())));
        content.append(EmailTemplateBuilder.infoBox("Price per coin", "$" + event.getPrice()));
        content.append(EmailTemplateBuilder.infoBox("Total Value", "$" + totalValue));
        content.append(EmailTemplateBuilder.infoBox("Status", event.getStatus()));
        content.append(EmailTemplateBuilder.infoBox("Timestamp", formatIst(event.getTimestamp())));
        content.append(EmailTemplateBuilder.ctaButton(frontendUrl + "/portfolio", "View Portfolio", "#ffd700"));

        String footer = EmailTemplateBuilder.footer(appName, frontendUrl);
        String body = EmailTemplateBuilder.wrap(appName, logoUrl, content.toString(), footer);

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendWithdrawalStatusEmail(String toEmail, String fullName, WithdrawalStatusEvent event) {
        String subject = "💸 Withdrawal " + event.getStatus();

        StringBuilder content = new StringBuilder();
        content.append(EmailTemplateBuilder.greeting(fullName));
        
        String status = event.getStatus();
        if ("SUCCESS".equalsIgnoreCase(status)) {
            content.append(EmailTemplateBuilder.para("Your withdrawal has been approved and processed!"));
        } else if ("DECLINED".equalsIgnoreCase(status)) {
            content.append(EmailTemplateBuilder.para("Your withdrawal request has been declined."));
        } else {
            content.append(EmailTemplateBuilder.para("Your withdrawal request is under review."));
        }

        content.append(EmailTemplateBuilder.infoBox("Amount", "$" + event.getAmount()));
        content.append(EmailTemplateBuilder.infoBox("Status", event.getStatus()));
        content.append(EmailTemplateBuilder.ctaButton(frontendUrl + "/wallet", "View Wallet", "#ffd700"));

        String footer = EmailTemplateBuilder.footer(appName, frontendUrl);
        String body = EmailTemplateBuilder.wrap(appName, logoUrl, content.toString(), footer);

        sendEmail(toEmail, subject, body);
    }

    private String formatIst(LocalDateTime timestamp) {
        if (timestamp == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return timestamp.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                .format(formatter) + " IST";
    }

    private String maskEmail(String email) {
        if (email == null || email.length() < 4) return "****";
        return email.substring(0, 3) + "***" + email.substring(email.indexOf("@"));
    }
}
