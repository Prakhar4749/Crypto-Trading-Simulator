package com.prakhar.common.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtil {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern(
          "dd-MM-yyyy hh:mm:ss a"
        );
    private static final ZoneId IST = 
        ZoneId.of("Asia/Kolkata");

    // ═══ TIMESTAMP ═══
    public static String now() {
        return ZonedDateTime.now(IST).format(FORMATTER) 
          + " IST";
    }

    // ═══ MASKING UTILS ═══
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) 
          return "***";
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        String masked = local.length() > 3
            ? local.substring(0, 3) + "***"
            : "***";
        return masked + "@" + domain;
    }

    public static String maskPassword(String password) {
        return "********";
    }

    public static String maskJwt(String token) {
        if (token == null || token.length() < 10) 
          return "***";
        return token.substring(0, 10) + "...";
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) 
          return "***";
        return "******" + phone.substring(
          phone.length() - 4);
    }

    public static String maskKey(String key) {
        if (key == null || key.length() < 8) 
          return "***";
        return key.substring(0, 4) + "***" 
          + key.substring(key.length() - 4);
    }

    // ═══ LOG BUILDERS (Using concatenation to avoid String.format issues with % characters) ═══
    public static String info(String service, 
        String path, String userId, String message) {
        return now() + " | INFO  | " + service + " | " + path + " | userId=" + (userId != null ? userId : "anonymous") + " | " + message;
    }

    public static String warn(String service,
        String path, String userId, String message) {
        return now() + " | WARN  | " + service + " | " + path + " | userId=" + (userId != null ? userId : "anonymous") + " | " + message;
    }

    public static String error(String service,
        String path, String userId, String reason) {
        return now() + " | ERROR | " + service + " | " + path + " | userId=" + (userId != null ? userId : "anonymous") + " | reason=" + reason;
    }

    public static String slow(String service,
        String path, String userId, long ms) {
        return now() + " | SLOW  | " + service + " | " + path + " | userId=" + (userId != null ? userId : "anonymous") + " | ⚠️ SLOW REQUEST: " + ms + "ms";
    }

    public static String debug(String service,
        String path, String userId, 
        String field, Object value) {
        return now() + " | DEBUG | " + service + " | " + path + " | userId=" + (userId != null ? userId : "anonymous") + " | " + field + "=" + value;
    }
}
