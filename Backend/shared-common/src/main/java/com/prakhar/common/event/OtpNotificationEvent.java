package com.prakhar.common.event;

import com.prakhar.common.enums.VerificationType;

public class OtpNotificationEvent {
    private Long userId;
    private String email;
    private String otp;
    private VerificationType verificationType;
    private String bonusClaimToken;
    private String fullName;
    private String eventType;

    public OtpNotificationEvent() {}

    public OtpNotificationEvent(Long userId, String email, String otp, VerificationType verificationType) {
        this.userId = userId;
        this.email = email;
        this.otp = otp;
        this.verificationType = verificationType;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public VerificationType getVerificationType() { return verificationType; }
    public void setVerificationType(VerificationType verificationType) { this.verificationType = verificationType; }

    public String getBonusClaimToken() { return bonusClaimToken; }
    public void setBonusClaimToken(String bonusClaimToken) { this.bonusClaimToken = bonusClaimToken; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}
