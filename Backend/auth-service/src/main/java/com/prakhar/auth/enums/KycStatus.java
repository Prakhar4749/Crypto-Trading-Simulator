package com.prakhar.auth.enums;

public enum KycStatus {
    NOT_STARTED,    // user just registered
    PENDING,        // user submitted info, under review
    VERIFIED,       // email + phone + address all done
    REJECTED        // rejected (future use)
}
