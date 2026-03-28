package com.prakhar.notification.consumer;

import com.prakhar.common.event.WithdrawalStatusEvent;
import com.prakhar.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WithdrawalNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawalNotificationConsumer.class);
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public WithdrawalNotificationConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.withdrawal-status}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String rawMessage) {
        logger.info("=== WITHDRAWAL NOTIFICATION CONSUMER TRIGGERED ===");
        try {
            WithdrawalStatusEvent event = objectMapper.readValue(rawMessage, WithdrawalStatusEvent.class);
            logger.info("Consumed withdrawal event for email: {}", event.getEmail());
            if (event.getEmail() == null) {
                logger.warn("Withdrawal event has no email for userId: {}", event.getUserId());
                return;
            }
            emailService.sendWithdrawalStatusEmail(event.getEmail(), event.getFullName(), event);
        } catch (Exception e) {
            logger.error("Error processing withdrawal status email: {}", e.getMessage());
        }
    }
}
