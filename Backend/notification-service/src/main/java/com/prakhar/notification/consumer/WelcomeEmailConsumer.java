package com.prakhar.notification.consumer;

import com.prakhar.common.event.UserCreatedEvent;
import com.prakhar.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WelcomeEmailConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeEmailConsumer.class);
    private final EmailService emailService;

    public WelcomeEmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${kafka.topic.user-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(UserCreatedEvent event) {
        logger.info("Consumed user-created event for email: {}", event.getEmail());
        try {
            emailService.sendWelcomeEmail(
                event.getEmail(),
                event.getFullName(),
                event.getBonusClaimToken(),
                event.isEmailVerified()
            );
        } catch (Exception e) {
            logger.error("Error processing welcome email: {}", e.getMessage());
        }
    }
}
