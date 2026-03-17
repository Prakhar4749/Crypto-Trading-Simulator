package com.prakhar.auth.service;

import com.prakhar.common.enums.VerificationType;
import java.util.Map;

public interface AuthService {
    String signup(String fullName, String email, String mobile, String password);
    Map<String, Object> signin(String email, String password);
    Map<String, Object> verifySigninOtp(String otp, String sessionId) throws Exception;
    void sendVerificationCode(Long userId, String email, VerificationType type);
    void verifyEmail(Long userId, String otp) throws Exception;
    String sendForgotPasswordOtp(String email) throws Exception;
    void resetPassword(String sessionId, String otp, String newPassword) throws Exception;
    void updateTwoFactorStatus(Long userId, boolean status);
}
