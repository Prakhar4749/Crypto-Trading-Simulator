package com.prakhar.auth.entity;

import com.prakhar.common.enums.VerificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "forgot_password_tokens")
public class ForgotPasswordToken {
    @Id
    private String id;

    @Column(name = "user_id")
    private Long userId;

    private String otp;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private VerificationType verificationType;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    public ForgotPasswordToken() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public VerificationType getVerificationType() { return verificationType; }
    public void setVerificationType(VerificationType verificationType) { this.verificationType = verificationType; }
    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }
}
