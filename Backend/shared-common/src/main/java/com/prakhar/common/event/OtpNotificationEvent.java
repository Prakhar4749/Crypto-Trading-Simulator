package com.prakhar.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.prakhar.common.enums.VerificationType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpNotificationEvent {
    private Long userId;
    private String email;
    private String fullName;
    private String otp;
    private VerificationType verificationType;
    private String eventType;
    private String bonusClaimToken;

    public OtpNotificationEvent() {}

    public OtpNotificationEvent(Long userId, String email, String otp, VerificationType verificationType) {
        this.userId = userId;
        this.email = email;
        this.otp = otp;
        this.verificationType = verificationType;
    }

    public OtpNotificationEvent(Long userId, String email, String fullName, String otp, VerificationType verificationType, String eventType, String bonusClaimToken) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.otp = otp;
        this.verificationType = verificationType;
        this.eventType = eventType;
        this.bonusClaimToken = bonusClaimToken;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public VerificationType getVerificationType() { return verificationType; }
    public void setVerificationType(VerificationType verificationType) { this.verificationType = verificationType; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getBonusClaimToken() { return bonusClaimToken; }
    public void setBonusClaimToken(String bonusClaimToken) { this.bonusClaimToken = bonusClaimToken; }
}
