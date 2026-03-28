package com.prakhar.common.dto;

public class PaymentOrderResponse {
    private String orderId;
    private Long amount;
    private String currency;
    private String status;

    public PaymentOrderResponse() {}

    public PaymentOrderResponse(String orderId, Long amount, String currency, String status) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
