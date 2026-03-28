package com.prakhar.common.dto;

public class AssetDTO {
    private Long id;
    private double quantity;
    private double buyPrice;
    private String coinId;
    private Long userId;
    private Object coin;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }
    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Object getCoin() { return coin; }
    public void setCoin(Object coin) { this.coin = coin; }
}
