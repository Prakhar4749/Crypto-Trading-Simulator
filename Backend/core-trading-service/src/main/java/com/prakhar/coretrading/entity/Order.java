package com.prakhar.coretrading.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String status;

    @Column(name = "coin_id", nullable = false)
    private String coinId;

    @Column(nullable = false)
    private double quantity;

    @Column(name = "buy_price")
    private double buyPrice;

    @Column(name = "sell_price")
    private double sellPrice;

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }
    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) { this.sellPrice = sellPrice; }
}
