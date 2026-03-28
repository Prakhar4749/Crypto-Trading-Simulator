package com.prakhar.notification.consumer;

import com.prakhar.common.event.OtpNotificationEvent;
import com.prakhar.notification.service.EmailService;
import com.prakhar.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class OtpNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OtpNotificationConsumer.class);
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public OtpNotificationConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.otp-notification}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String rawMessage) {
        // THIS MUST PRINT ON EVERY MESSAGE
        System.out.println("##############################");
        System.out.println("OTP CONSUMER FIRED: " + rawMessage);
        System.out.println("##############################");

        logger.info("=== OTP CONSUMER TRIGGERED ===");
        logger.info("Raw message: {}", rawMessage);

        try {
            OtpNotificationEvent event = objectMapper.readValue(rawMessage, OtpNotificationEvent.class);
            
            if (event == null) {
                logger.warn("Parsed event is null");
                return;
            }

            logger.info(LogUtil.info(
                "notification-service",
                "OtpConsumer",
                event.getUserId() != null ? event.getUserId().toString() : null,
                "Processing OTP event | type=" + event.getEventType()
            ));

            processEvent(event);

        } catch (Exception e) {
            logger.error("Failed to parse or process OTP event: {}", e.getMessage(), e);
        }
    }

    private void processEvent(OtpNotificationEvent event) {
        String eventType = event.getEventType();
        if (eventType == null) {
            logger.warn("OTP event has null eventType");
            return;
        }

        String fullName = event.getFullName() != null ? event.getFullName() : "User";

        switch (eventType.toUpperCase()) {
            case "EMAIL_VERIFICATION":
            case "EMAIL":
                emailService.sendOtpEmail(
                    event.getEmail(),
                    fullName,
                    event.getOtp(),
                    "EMAIL_VERIFY"
                );
                break;

            case "TWO_FACTOR":
            case "TWO_FACTOR_AUTH":
                emailService.sendOtpEmail(
                    event.getEmail(),
                    fullName,
                    event.getOtp(),
                    "TWO_FACTOR"
                );
                break;

            case "FORGOT_PASSWORD":
            case "PASSWORD_RESET":
                emailService.sendOtpEmail(
                    event.getEmail(),
                    fullName,
                    event.getOtp(),
                    "FORGOT_PASSWORD"
                );
                break;

            case "CLAIM_BONUS_EMAIL":
                emailService.sendClaimBonusEmail(
                    event.getEmail(),
                    fullName,
                    event.getBonusClaimToken()
                );
                break;

            default:
                logger.warn("Unknown event type: {}", eventType);
        }
    }
}
