package com.prakhar.common.util;

import com.prakhar.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class RoleValidator {
    public static void requireAdmin(String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new CustomException("Admin access required", HttpStatus.FORBIDDEN);
        }
    }
}
