package com.prakhar.common.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends BaseException {
    public PaymentException(String reason) {
        super("Payment processing failed: " + reason, HttpStatus.UNPROCESSABLE_ENTITY, "PAYMENT_ERROR");
    }
}
