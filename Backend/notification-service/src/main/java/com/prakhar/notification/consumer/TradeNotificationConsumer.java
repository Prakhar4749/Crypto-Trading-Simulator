package com.prakhar.notification.consumer;

import com.prakhar.common.event.TradeExecutedEvent;
import com.prakhar.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TradeNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TradeNotificationConsumer.class);
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public TradeNotificationConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.trade-executed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String rawMessage) {
        logger.info("=== TRADE NOTIFICATION CONSUMER TRIGGERED ===");
        try {
            TradeExecutedEvent event = objectMapper.readValue(rawMessage, TradeExecutedEvent.class);
            logger.info("Consumed trade-executed event for email: {}", event.getEmail());
            if (event.getEmail() == null) {
                logger.warn("Trade event has no email for userId: {}", event.getUserId());
                return;
            }
            emailService.sendTradeConfirmationEmail(event.getEmail(), event.getFullName(), event);
        } catch (Exception e) {
            logger.error("Error processing trade confirmation email: {}", e.getMessage());
        }
    }
}
