package com.prakhar.common.exception;

import org.springframework.http.HttpStatus;
import java.math.BigDecimal;

public class InsufficientBalanceException extends BaseException {
    public InsufficientBalanceException(BigDecimal required, BigDecimal available) {
        super("Insufficient balance. Required: $" + required + ", Available: $" + available, 
              HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_BALANCE");
    }
}
