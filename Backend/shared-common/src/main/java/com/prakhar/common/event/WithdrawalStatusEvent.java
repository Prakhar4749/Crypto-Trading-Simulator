package com.prakhar.common.event;

import java.math.BigDecimal;

public class WithdrawalStatusEvent {
    private Long withdrawalId;
    private Long userId;
    private String email;
    private String fullName;
    private BigDecimal amount;
    private String status;

    public WithdrawalStatusEvent() {}

    public WithdrawalStatusEvent(Long withdrawalId, Long userId, String email, String fullName, BigDecimal amount, String status) {
        this.withdrawalId = withdrawalId;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.amount = amount;
        this.status = status;
    }

    public Long getWithdrawalId() { return withdrawalId; }
    public void setWithdrawalId(Long withdrawalId) { this.withdrawalId = withdrawalId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
