package com.prakhar.auth.controller;

import com.prakhar.auth.dto.request.GoogleAuthRequest;
import com.prakhar.auth.dto.request.SigninRequest;
import com.prakhar.auth.dto.request.SignupRequest;
import com.prakhar.auth.entity.User;
import com.prakhar.auth.mapper.UserMapper;
import com.prakhar.auth.repository.UserRepository;
import com.prakhar.auth.service.AuthService;
import com.prakhar.auth.service.GoogleAuthService;
import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.UserDTO;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.enums.VerificationType;
import com.prakhar.common.exception.ResourceNotFoundException;
import com.prakhar.common.util.LogUtil;
import com.prakhar.auth.dto.request.UpdateProfileRequest;
import com.prakhar.auth.dto.request.SendResetOtpRequest;
import com.prakhar.auth.dto.request.VerifyResetOtpRequest;
import com.prakhar.auth.utils.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Value("${spring.application.name:auth-service}")
    private String serviceName;

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, GoogleAuthService googleAuthService, 
                          UserRepository userRepository, UserMapper userMapper) {
        this.authService = authService;
        this.googleAuthService = googleAuthService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<Map<String, String>>> signup(@Valid @RequestBody SignupRequest request) {
        log.info(LogUtil.info(
            serviceName,
            "POST /auth/signup",
            null,
            "Signup attempt | email=" + 
              LogUtil.maskEmail(request.getEmail())
        ));

        String token = authService.signup(
                request.getFullName(),
                request.getEmail(),
                request.getMobile(),
                request.getPassword()
        );
        return ResponseEntity.ok(ApiResponse.success("Signup Success", Map.of("jwt", token)));
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> signin(@Valid @RequestBody SigninRequest request) {
        log.info(LogUtil.info(
            serviceName,
            "POST /auth/signin", 
            null,
            "Login attempt | email=" + 
              LogUtil.maskEmail(request.getEmail())
        ));

        Map<String, Object> response = authService.signin(
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(ApiResponse.success("Signin process initiated", response));
    }

    @PostMapping("/auth/google")
    public ResponseEntity<ApiResponse<Map<String, Object>>> googleLogin(@Valid @RequestBody GoogleAuthRequest request) {
        Map<String, Object> response = googleAuthService.authenticateWithGoogle(request.getIdToken());
        return ResponseEntity.ok(ApiResponse.success("Google login successful", response));
    }

    @GetMapping("/api/users/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@RequestHeader("X-User-ID") Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", userMapper.toDTO(user)));
    }

    @PostMapping("/auth/two-factor/otp/{otp}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyTwoFactor(
            @PathVariable String otp, 
            @RequestBody Map<String, String> request) throws Exception {
        Map<String, Object> response = authService.verifySigninOtp(otp, request.get("id"));
        return ResponseEntity.ok(ApiResponse.success("2FA verified successfully", response));
    }

    @PostMapping("/auth/users/verification/{verificationType}/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendVerificationOtp(
            @PathVariable("verificationType") VerificationType verificationType,
            @RequestHeader("X-User-ID") Long userId) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));
        
        authService.sendVerificationCode(userId, user.getEmail(), verificationType);
        return ResponseEntity.ok(ApiResponse.success("Verification OTP sent", null));
    }

    @PatchMapping("/auth/users/verify-email/verify-otp/{otp}")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @PathVariable String otp,
            @RequestHeader("X-User-ID") Long userId) throws Exception {
        authService.verifyEmail(userId, otp);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<ApiResponse<String>> sendForgotPasswordOtp(@Valid @RequestBody SendResetOtpRequest request) throws Exception {
        String sessionId = authService.sendForgotPasswordOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Password reset OTP sent", sessionId));
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody VerifyResetOtpRequest request) throws Exception {
        authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", null));
    }

    @PatchMapping("/api/users/enable-two-factor")
    public ResponseEntity<ApiResponse<Void>> updateTwoFactorStatus(
            @RequestHeader("X-User-ID") Long userId,
            @RequestParam boolean enabled) {
        authService.updateTwoFactorStatus(userId, enabled);
        return ResponseEntity.ok(ApiResponse.success("2FA status updated", null));
    }

    @PatchMapping("/api/users/set-password")
    public ResponseEntity<ApiResponse<Void>> setPassword(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody Map<String, String> request) {
        authService.setPassword(userId, request.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success("Password set successfully", null));
    }

    // Update profile + auto-check KYC
    @PatchMapping("/api/users/profile/update")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {

        Long userId = RequestContext.getUserId(httpRequest);
        UserDTO updated = authService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    // Get KYC status
    @GetMapping("/api/users/kyc/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getKycStatus(
            HttpServletRequest httpRequest) {

        Long userId = RequestContext.getUserId(httpRequest);
        Map<String, Object> status = authService.getKycStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("KYC status fetched", status));
    }

    // NO JWT REQUIRED — token IS the auth
    @PostMapping("/auth/claim-bonus")
    public ResponseEntity<ApiResponse<WalletDTO>> claimBonus(@RequestParam("token") String token) {
        WalletDTO wallet = authService.claimSignupBonus(token);
        return ResponseEntity.ok(ApiResponse.success("🎉 Bonus credited to your wallet!", wallet));
    }

    @PostMapping("/api/users/resend-bonus-link")
    public ResponseEntity<ApiResponse<Void>> resendBonusLink(@RequestHeader("X-User-ID") Long userId) {
        authService.resendBonusLink(userId);
        return ResponseEntity.ok(ApiResponse.success("Bonus claim link resent successfully", null));
    }

    @GetMapping("/internal/users/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
            @PathVariable Long userId,
            @RequestHeader("X-Internal-Api-Key") String apiKey) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id=" + userId));
        return ResponseEntity.ok(ApiResponse.success("User found", userMapper.toDTO(user)));
    }
}
