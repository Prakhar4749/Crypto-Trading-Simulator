package com.prakhar.notification.consumer;

import com.prakhar.common.event.TradeExecutedEvent;
import com.prakhar.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TradeNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TradeNotificationConsumer.class);
    private final EmailService emailService;

    public TradeNotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${kafka.topic.trade}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(TradeExecutedEvent event) {
        logger.info("Consumed trade-executed event for email: {}", event.getEmail());
        if (event.getEmail() == null) {
            logger.warn("Trade event has no email for userId: {}", event.getUserId());
            return;
        }
        try {
            emailService.sendTradeConfirmationEmail(event.getEmail(), event.getFullName(), event);
        } catch (Exception e) {
            logger.error("Error processing trade confirmation email: {}", e.getMessage());
        }
    }
}
