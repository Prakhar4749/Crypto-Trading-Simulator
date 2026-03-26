package com.prakhar.coretrading.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_details")
public class PaymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private String ifsc;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    public PaymentDetails() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public String getIfsc() { return ifsc; }
    public void setIfsc(String ifsc) { this.ifsc = ifsc; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
