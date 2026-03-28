package com.prakhar.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeExecutedEvent {
    private Long userId;
    private String email;
    private String fullName;
    private String coinId;
    private double quantity;
    private BigDecimal price;
    private String orderType;
    private String status;
    private LocalDateTime timestamp;

    public TradeExecutedEvent() {}

    public TradeExecutedEvent(Long userId, String email, String fullName, String coinId, double quantity, BigDecimal price, String orderType, String status, LocalDateTime timestamp) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.coinId = coinId;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
