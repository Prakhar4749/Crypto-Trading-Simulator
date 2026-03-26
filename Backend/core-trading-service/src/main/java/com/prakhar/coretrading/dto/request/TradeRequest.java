package com.prakhar.coretrading.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TradeRequest {
    @NotBlank(message = "Coin ID is required")
    private String coinId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than 0")
    private Double quantity;

    @NotBlank(message = "Order type is required")
    private String orderType;

    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
}
