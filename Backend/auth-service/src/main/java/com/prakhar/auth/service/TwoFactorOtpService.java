package com.prakhar.auth.service;

import com.prakhar.auth.entity.TwoFactorOTP;
import java.util.Optional;

public interface TwoFactorOtpService {
    TwoFactorOTP createOtp(Long userId, String email, String fullName, String jwt);
    Optional<TwoFactorOTP> findByUserId(Long userId);
    Optional<TwoFactorOTP> findById(String id);
    boolean verifyOtp(TwoFactorOTP otp, String code);
    void deleteOtp(String id);
}
