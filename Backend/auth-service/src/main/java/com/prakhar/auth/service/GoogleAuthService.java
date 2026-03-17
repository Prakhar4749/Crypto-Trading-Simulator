package com.prakhar.auth.service;

import java.util.Map;

public interface GoogleAuthService {
    Map<String, Object> authenticateWithGoogle(String googleIdToken);
}
