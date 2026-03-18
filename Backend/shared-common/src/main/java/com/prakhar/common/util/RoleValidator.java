package com.prakhar.common.util;

import com.prakhar.common.exception.ForbiddenException;

public class RoleValidator {
    public static void requireAdmin(String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new ForbiddenException("Admin access required for this operation");
        }
    }
}
