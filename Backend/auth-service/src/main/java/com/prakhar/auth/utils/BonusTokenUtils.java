package com.prakhar.auth.utils;

import java.time.LocalDateTime;
import java.util.UUID;

public class BonusTokenUtils {

    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        // 64 char hex string — hard to guess
    }

    public static LocalDateTime getExpiry() {
        return LocalDateTime.now().plusHours(24);
    }
}
