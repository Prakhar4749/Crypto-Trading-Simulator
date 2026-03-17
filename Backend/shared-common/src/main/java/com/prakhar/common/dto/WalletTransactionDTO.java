package com.prakhar.common.dto;

import com.prakhar.common.enums.WalletTransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletTransactionDTO {
    private Long id;
    private Long walletId;
    private WalletTransactionType type;
    private LocalDateTime date;
    private String transferId;
    private String purpose;
    private BigDecimal amount;

    public WalletTransactionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }

    public WalletTransactionType getType() { return type; }
    public void setType(WalletTransactionType type) { this.type = type; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getTransferId() { return transferId; }
    public void setTransferId(String transferId) { this.transferId = transferId; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
