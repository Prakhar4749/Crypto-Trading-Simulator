package com.prakhar.common.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends BaseException {
    public ExternalServiceException(String serviceName, String reason) {
        super(serviceName + " is unavailable: " + reason, HttpStatus.SERVICE_UNAVAILABLE, "EXTERNAL_SERVICE_ERROR");
    }
}
