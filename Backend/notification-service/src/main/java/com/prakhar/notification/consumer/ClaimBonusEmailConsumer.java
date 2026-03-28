package com.prakhar.notification.consumer;

import com.prakhar.common.event.OtpNotificationEvent;
import com.prakhar.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClaimBonusEmailConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ClaimBonusEmailConsumer.class);
    private final EmailService emailService;

    public ClaimBonusEmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${kafka.topic.otp}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OtpNotificationEvent event) {
        if ("CLAIM_BONUS_EMAIL".equals(event.getEventType())) {
            logger.info("Consumed claim-bonus-email event for email: {}", event.getEmail());
            try {
                emailService.sendClaimBonusEmail(
                    event.getEmail(),
                    event.getFullName(),
                    event.getBonusClaimToken()
                );
            } catch (Exception e) {
                logger.error("Error processing claim bonus email: {}", e.getMessage());
            }
        }
    }
}
