package com.prakhar.auth.controller;

import com.prakhar.auth.dto.request.GoogleAuthRequest;
import com.prakhar.auth.entity.User;
import com.prakhar.auth.mapper.UserMapper;
import com.prakhar.auth.repository.UserRepository;
import com.prakhar.auth.service.AuthService;
import com.prakhar.auth.service.GoogleAuthService;
import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.UserDTO;
import com.prakhar.common.enums.VerificationType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

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
    public ResponseEntity<ApiResponse<Map<String, String>>> signup(@RequestBody Map<String, String> request) {
        String token = authService.signup(
                request.get("fullName"),
                request.get("email"),
                request.get("mobile"),
                request.get("password")
        );
        return ResponseEntity.ok(ApiResponse.success("Signup Success", Map.of("jwt", token)));
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> signin(@RequestBody Map<String, String> request) {
        Map<String, Object> response = authService.signin(
                request.get("email"),
                request.get("password")
        );
        return ResponseEntity.ok(ApiResponse.success("Signin process initiated", response));
    }

    @PostMapping("/auth/google")
    public ResponseEntity<ApiResponse<Map<String, Object>>> googleLogin(@RequestBody GoogleAuthRequest request) {
        Map<String, Object> response = googleAuthService.authenticateWithGoogle(request.getIdToken());
        return ResponseEntity.ok(ApiResponse.success("Google login successful", response));
    }

    @GetMapping("/api/users/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@RequestHeader("X-User-ID") Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", userMapper.toDTO(user)));
    }

    @PostMapping("/auth/two-factor/otp/{otp}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyTwoFactor(
            @PathVariable String otp, 
            @RequestParam String id) throws Exception {
        Map<String, Object> response = authService.verifySigninOtp(otp, id);
        return ResponseEntity.ok(ApiResponse.success("2FA verified successfully", response));
    }

    @PostMapping("/auth/users/verification/{verificationType}/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendVerificationOtp(
            @PathVariable VerificationType verificationType,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Email") String email) {
        authService.sendVerificationCode(userId, email, verificationType);
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
    public ResponseEntity<ApiResponse<String>> sendForgotPasswordOtp(@RequestBody Map<String, String> request) throws Exception {
        String sessionId = authService.sendForgotPasswordOtp(request.get("email"));
        return ResponseEntity.ok(ApiResponse.success("Password reset OTP sent", sessionId));
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String sessionId,
            @RequestParam String otp,
            @RequestBody Map<String, String> request) throws Exception {
        authService.resetPassword(sessionId, otp, request.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", null));
    }

    @PatchMapping("/api/users/enable-two-factor")
    public ResponseEntity<ApiResponse<Void>> updateTwoFactorStatus(
            @RequestHeader("X-User-ID") Long userId,
            @RequestParam boolean enabled) {
        authService.updateTwoFactorStatus(userId, enabled);
        return ResponseEntity.ok(ApiResponse.success("2FA status updated", null));
    }
}
