package com.prakhar.notification.consumer;

import com.prakhar.common.event.OtpNotificationEvent;
import com.prakhar.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OtpNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OtpNotificationConsumer.class);
    private final EmailService emailService;

    public OtpNotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${kafka.topic.otp}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OtpNotificationEvent event) {
        logger.info("Consumed OTP event for email: {}", event.getEmail());
        try {
            emailService.sendOtpEmail(event.getEmail(), event.getOtp(), event.getVerificationType().name());
        } catch (Exception e) {
            logger.error("Error processing OTP event: {}", e.getMessage());
        }
    }
}
