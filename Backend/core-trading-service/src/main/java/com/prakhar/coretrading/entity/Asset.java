package com.prakhar.coretrading.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "asset", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "coin_id"}))
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coin_id", nullable = false)
    private String coinId;

    @Column(nullable = false)
    private double quantity;

    @Column(name = "buy_price", nullable = false)
    private double buyPrice;

    public Asset() {}

    public Asset(Long id, Long userId, String coinId, double quantity, double buyPrice) {
        this.id = id;
        this.userId = userId;
        this.coinId = coinId;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }
}
