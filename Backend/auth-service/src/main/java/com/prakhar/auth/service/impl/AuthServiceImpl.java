package com.prakhar.auth.service.impl;

import com.prakhar.auth.entity.ForgotPasswordToken;
import com.prakhar.auth.entity.TwoFactorOTP;
import com.prakhar.auth.entity.User;
import com.prakhar.auth.entity.VerificationCode;
import com.prakhar.auth.repository.ForgotPasswordTokenRepository;
import com.prakhar.auth.repository.TwoFactorOtpRepository;
import com.prakhar.auth.repository.UserRepository;
import com.prakhar.auth.repository.VerificationCodeRepository;
import com.prakhar.auth.service.AuthService;
import com.prakhar.auth.service.TwoFactorOtpService;
import com.prakhar.auth.utils.JwtProvider;
import com.prakhar.auth.utils.OtpUtils;
import com.prakhar.common.enums.VerificationType;
import com.prakhar.common.event.OtpNotificationEvent;
import com.prakhar.common.event.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TwoFactorOtpService twoFactorOtpService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                           KafkaTemplate<String, Object> kafkaTemplate, TwoFactorOtpService twoFactorOtpService,
                           VerificationCodeRepository verificationCodeRepository,
                           ForgotPasswordTokenRepository forgotPasswordTokenRepository,
                           JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.twoFactorOtpService = twoFactorOtpService;
        this.verificationCodeRepository = verificationCodeRepository;
        this.forgotPasswordTokenRepository = forgotPasswordTokenRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @Transactional
    public String signup(String fullName, String email, String mobile, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");
        user.setVerified(false);
        user.setTwoFactorEnabled(false);

        User savedUser = userRepository.save(user);

        kafkaTemplate.send("user-created", new UserCreatedEvent(savedUser.getId(), savedUser.getEmail(), savedUser.getFullName()));

        return jwtProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
    }

    @Override
    public Map<String, Object> signin(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        Map<String, Object> response = new HashMap<>();
        
        String jwt = jwtProvider.generateToken(user.getId(), user.getEmail(), user.getRole());

        if (user.isTwoFactorEnabled()) {
            TwoFactorOTP twoFactorOTP = twoFactorOtpService.createOtp(user.getId(), user.getEmail(), jwt);
            response.put("twoFactorAuthEnabled", true);
            response.put("session", twoFactorOTP.getId());
            response.put("message", "Two factor authentication enabled");
        } else {
            response.put("jwt", jwt);
            response.put("message", "Login Success");
        }
        
        return response;
    }

    @Override
    public Map<String, Object> verifySigninOtp(String otp, String sessionId) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(sessionId)
                .orElseThrow(() -> new Exception("Invalid session"));

        if (!twoFactorOtpService.verifyOtp(twoFactorOTP, otp)) {
            throw new Exception("Invalid or expired OTP");
        }

        twoFactorOtpService.deleteOtp(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", twoFactorOTP.getJwt());
        response.put("message", "Two factor authentication verified");
        return response;
    }

    @Override
    @Transactional
    public void sendVerificationCode(Long userId, String email, VerificationType type) {
        String code = OtpUtils.generateOTP();
        verificationCodeRepository.deleteByUserId(userId);
        
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUserId(userId);
        verificationCode.setEmail(email);
        verificationCode.setOtp(code);
        verificationCode.setVerificationType(type);
        verificationCode.setExpiryTime(LocalDateTime.now().plusMinutes(10)); // Fixed 10 for now or fetch from config
        
        verificationCodeRepository.save(verificationCode);
        kafkaTemplate.send("otp-notification", new OtpNotificationEvent(userId, email, code, type));
    }

    @Override
    @Transactional
    public void verifyEmail(Long userId, String otp) throws Exception {
        VerificationCode code = verificationCodeRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("Verification code not found"));

        if (code.getExpiryTime().isBefore(LocalDateTime.now()) || !code.getOtp().equals(otp)) {
            throw new Exception("Invalid or expired OTP");
        }

        User user = userRepository.findById(userId).orElseThrow();
        user.setVerified(true);
        userRepository.save(user);
        
        verificationCodeRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public String sendForgotPasswordOtp(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        String code = OtpUtils.generateOTP();
        forgotPasswordTokenRepository.deleteByUserId(user.getId());
        
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setId(java.util.UUID.randomUUID().toString());
        token.setUserId(user.getId());
        token.setEmail(email);
        token.setOtp(code);
        token.setVerificationType(VerificationType.FORGOT_PASSWORD);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(10));

        forgotPasswordTokenRepository.save(token);
        
        kafkaTemplate.send("otp-notification", new OtpNotificationEvent(user.getId(), email, code, VerificationType.FORGOT_PASSWORD));
        return token.getId();
    }

    @Override
    @Transactional
    public void resetPassword(String sessionId, String otp, String newPassword) throws Exception {
        ForgotPasswordToken token = forgotPasswordTokenRepository.findById(sessionId)
                .orElseThrow(() -> new Exception("Invalid session"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now()) || !token.getOtp().equals(otp)) {
            throw new Exception("Invalid or expired OTP");
        }

        User user = userRepository.findById(token.getUserId()).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        forgotPasswordTokenRepository.delete(token);
    }

    @Override
    public void updateTwoFactorStatus(Long userId, boolean status) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setTwoFactorEnabled(status);
        userRepository.save(user);
    }
}
