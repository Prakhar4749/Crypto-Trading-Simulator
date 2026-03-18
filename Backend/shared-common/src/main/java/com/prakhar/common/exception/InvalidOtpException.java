package com.prakhar.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends BaseException {
    public InvalidOtpException(String reason) {
        super("OTP validation failed: " + reason, HttpStatus.BAD_REQUEST, "INVALID_OTP");
    }
}
