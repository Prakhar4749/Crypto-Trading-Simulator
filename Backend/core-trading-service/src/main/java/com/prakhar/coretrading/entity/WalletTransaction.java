package com.prakhar.coretrading.entity;

import com.prakhar.common.enums.WalletTransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletTransactionType type;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "transfer_id")
    private String transferId;

    private String purpose;

    @Column(nullable = false)
    private BigDecimal amount;

    public WalletTransaction() {}

    public WalletTransaction(Long id, Long walletId, WalletTransactionType type, LocalDateTime date, String transferId, String purpose, BigDecimal amount) {
        this.id = id;
        this.walletId = walletId;
        this.type = type;
        this.date = date;
        this.transferId = transferId;
        this.purpose = purpose;
        this.amount = amount;
    }

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
