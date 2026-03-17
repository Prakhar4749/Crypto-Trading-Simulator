package com.prakhar.common.dto;

import com.prakhar.common.enums.WithdrawalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawalDTO {
    private Long id;
    private WithdrawalStatus status;
    private BigDecimal amount;
    private Long userId;
    private String email;
    private LocalDateTime date;

    public WithdrawalDTO() {}

    public WithdrawalDTO(Long id, WithdrawalStatus status, BigDecimal amount, Long userId, String email, LocalDateTime date) {
        this.id = id;
        this.status = status;
        this.amount = amount;
        this.userId = userId;
        this.email = email;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WithdrawalStatus getStatus() { return status; }
    public void setStatus(WithdrawalStatus status) { this.status = status; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
