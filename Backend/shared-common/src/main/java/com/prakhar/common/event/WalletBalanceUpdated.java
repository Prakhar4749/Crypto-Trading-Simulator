package com.prakhar.common.event;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceUpdated {
    private Long userId;
    private BigDecimal newBalance;
    private String transactionType;
}
