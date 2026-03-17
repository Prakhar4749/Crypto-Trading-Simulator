package com.prakhar.common.event;

import com.prakhar.common.enums.VerificationType;

public class OtpNotificationEvent {
    private Long userId;
    private String email;
    private String otp;
    private VerificationType verificationType;

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
}
