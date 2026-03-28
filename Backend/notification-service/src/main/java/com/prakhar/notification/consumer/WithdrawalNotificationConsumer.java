package com.prakhar.notification.consumer;

import com.prakhar.common.event.WithdrawalStatusEvent;
import com.prakhar.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawalNotificationConsumer.class);
    private final EmailService emailService;

    public WithdrawalNotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${kafka.topic.withdrawal}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(WithdrawalStatusEvent event) {
        logger.info("Consumed withdrawal event for email: {}", event.getEmail());
        if (event.getEmail() == null) {
            logger.warn("Withdrawal event has no email for userId: {}", event.getUserId());
            return;
        }
        try {
            emailService.sendWithdrawalStatusEmail(event.getEmail(), event.getFullName(), event);
        } catch (Exception e) {
            logger.error("Error processing withdrawal status email: {}", e.getMessage());
        }
    }
}
