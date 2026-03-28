package com.prakhar.notification.consumer;

import com.prakhar.common.event.UserCreatedEvent;
import com.prakhar.notification.service.EmailService;
import com.prakhar.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WelcomeEmailConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeEmailConsumer.class);
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public WelcomeEmailConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.user-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String rawMessage) {
        // THIS MUST PRINT ON EVERY MESSAGE
        System.out.println("##############################");
        System.out.println("WELCOME CONSUMER FIRED: " + rawMessage.substring(0, Math.min(50, rawMessage.length())));
        System.out.println("##############################");

        logger.info("=== WELCOME EMAIL CONSUMER TRIGGERED ===");
        logger.info("Raw message: {}", rawMessage);

        try {
            UserCreatedEvent event = objectMapper.readValue(rawMessage, UserCreatedEvent.class);
            
            if (event == null) {
                logger.warn("Parsed event is null");
                return;
            }

            logger.info(LogUtil.info(
                "notification-service",
                "WelcomeEmailConsumer",
                event.getUserId() != null ? event.getUserId().toString() : null,
                "Sending welcome email to: " + LogUtil.maskEmail(event.getEmail())
            ));

            emailService.sendWelcomeEmail(
                event.getEmail(),
                event.getFullName() != null ? event.getFullName() : "User",
                event.getBonusClaimToken(),
                event.isEmailVerified()
            );
        } catch (Exception e) {
            logger.error("Welcome email failed: {}", e.getMessage(), e);
        }
    }
}
