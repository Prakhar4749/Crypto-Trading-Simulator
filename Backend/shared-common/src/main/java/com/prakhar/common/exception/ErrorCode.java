package com.prakhar.common.exception;

public final class ErrorCode {
    // Auth errors
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String INVALID_OTP = "INVALID_OTP";
    public static final String OTP_EXPIRED = "OTP_EXPIRED";
    public static final String EMAIL_NOT_VERIFIED = "EMAIL_NOT_VERIFIED";

    // Resource errors  
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String WALLET_NOT_FOUND = "WALLET_NOT_FOUND";
    public static final String COIN_NOT_FOUND = "COIN_NOT_FOUND";

    // Business errors
    public static final String INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";
    public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
    public static final String INVALID_ORDER_TYPE = "INVALID_ORDER_TYPE";
    public static final String WITHDRAWAL_PENDING = "WITHDRAWAL_PENDING";
    public static final String PAYMENT_ERROR = "PAYMENT_ERROR";

    // External service errors
    public static final String EXTERNAL_SERVICE_ERROR = "EXTERNAL_SERVICE_ERROR";
    public static final String COINGECKO_ERROR = "COINGECKO_ERROR";
    public static final String RAZORPAY_ERROR = "RAZORPAY_ERROR";

    // Validation errors
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INVALID_INPUT = "INVALID_INPUT";

    // Server errors
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    private ErrorCode() {}
}
