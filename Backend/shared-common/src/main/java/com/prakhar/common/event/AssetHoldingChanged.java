package com.prakhar.common.event;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetHoldingChanged {
    private Long userId;
    private String coinId;
    private double newQuantity;
    private String changeType; // BUY/SELL
}
