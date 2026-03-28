package com.prakhar.auth.service;

import com.prakhar.auth.dto.request.UpdateProfileRequest;
import com.prakhar.common.dto.UserDTO;
import com.prakhar.common.dto.WalletDTO;
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
    void setPassword(Long userId, String newPassword);
    UserDTO updateProfile(Long userId, UpdateProfileRequest request);
    Map<String, Object> getKycStatus(Long userId);
    WalletDTO claimSignupBonus(String token);
}
