package com.prakhar.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreatedEvent {
    private Long userId;
    private String email;
    private String fullName;
    private String bonusClaimToken;
    private boolean emailVerified;

    public UserCreatedEvent() {}

    public UserCreatedEvent(Long userId, String email, String fullName, String bonusClaimToken, boolean emailVerified) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.bonusClaimToken = bonusClaimToken;
        this.emailVerified = emailVerified;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBonusClaimToken() { return bonusClaimToken; }
    public void setBonusClaimToken(String bonusClaimToken) { this.bonusClaimToken = bonusClaimToken; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
}
