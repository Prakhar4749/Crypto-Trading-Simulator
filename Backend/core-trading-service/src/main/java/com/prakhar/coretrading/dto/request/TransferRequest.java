package com.prakhar.coretrading.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {
    @NotNull(message = "Receiver wallet ID is required")
    private Long receiverWalletId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    private String purpose;

    public Long getReceiverWalletId() { return receiverWalletId; }
    public void setReceiverWalletId(Long receiverWalletId) { this.receiverWalletId = receiverWalletId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
