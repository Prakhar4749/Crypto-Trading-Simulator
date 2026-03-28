package com.prakhar.auth.service.impl;

import com.prakhar.auth.dto.request.CreateWalletRequest;
import com.prakhar.auth.dto.request.UpdateProfileRequest;
import com.prakhar.auth.entity.AuthProvider;
import com.prakhar.auth.entity.ForgotPasswordToken;
import com.prakhar.auth.entity.TwoFactorOTP;
import com.prakhar.auth.entity.User;
import com.prakhar.auth.entity.VerificationCode;
import com.prakhar.auth.enums.KycStatus;
import com.prakhar.auth.enums.UserRole;
import com.prakhar.auth.feign.CoreTradingClient;
import com.prakhar.auth.mapper.UserMapper;
import com.prakhar.auth.repository.ForgotPasswordTokenRepository;
import com.prakhar.auth.repository.TwoFactorOtpRepository;
import com.prakhar.auth.repository.UserRepository;
import com.prakhar.auth.repository.VerificationCodeRepository;
import com.prakhar.auth.service.AuthService;
import com.prakhar.auth.service.TwoFactorOtpService;
import com.prakhar.auth.utils.BonusTokenUtils;
import com.prakhar.auth.utils.JwtProvider;
import com.prakhar.auth.utils.OtpUtils;
import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.UserDTO;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.enums.VerificationType;
import com.prakhar.common.event.OtpNotificationEvent;
import com.prakhar.common.event.UserCreatedEvent;
import com.prakhar.common.exception.*;
import com.prakhar.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TwoFactorOtpService twoFactorOtpService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;
    private final CoreTradingClient coreTradingClient;

    @Value("${internal.api-key}")
    private String internalApiKey;

    @Value("${kafka.topic.user-created:user-created}")
    private String userCreatedTopic;

    @Value("${kafka.topic.otp-notification:otp-notification}")
    private String otpNotificationTopic;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                           KafkaTemplate<String, Object> kafkaTemplate, TwoFactorOtpService twoFactorOtpService,
                           VerificationCodeRepository verificationCodeRepository,
                           ForgotPasswordTokenRepository forgotPasswordTokenRepository,
                           JwtProvider jwtProvider, UserMapper userMapper,
                           CoreTradingClient coreTradingClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.twoFactorOtpService = twoFactorOtpService;
        this.verificationCodeRepository = verificationCodeRepository;
        this.forgotPasswordTokenRepository = forgotPasswordTokenRepository;
        this.jwtProvider = jwtProvider;
        this.userMapper = userMapper;
        this.coreTradingClient = coreTradingClient;
    }

    @Override
    @Transactional
    public String signup(String fullName, String email, String mobile, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("User with email " + maskEmail(email) + " already exists");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.ROLE_USER);
        user.setVerified(false);
        user.setTwoFactorEnabled(false);
        user.setAuthProvider(AuthProvider.LOCAL); 

        User savedUser = userRepository.save(user);
        
        if (savedUser == null) {
            throw new BusinessException("Failed to create user account. Please try again.");
        }

        // 2. Create wallet via Feign (SYNCHRONOUS)
        try {
            coreTradingClient.createWalletForUser(
                new CreateWalletRequest(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getFullName()
                )
            );
            log.info(LogUtil.info(
                "auth-service",
                "POST /auth/signup",
                savedUser.getId().toString(),
                "Wallet created via Feign for: " + maskEmail(savedUser.getEmail())
            ));
        } catch (Exception e) {
            log.error(LogUtil.error(
                "auth-service",
                "POST /auth/signup",
                null,
                "Wallet creation failed — rolling back: " + e.getMessage()
            ));
            userRepository.delete(savedUser);
            throw new ExternalServiceException(
                "core-trading-service",
                "Could not initialize your account. Please try again."
            );
        }

        // Generate bonus claim token
        String bonusToken = BonusTokenUtils.generateToken();
        savedUser.setBonusClaimToken(bonusToken);
        savedUser.setBonusClaimTokenExpiry(BonusTokenUtils.getExpiry());
        userRepository.save(savedUser);

        kafkaTemplate.send(userCreatedTopic, new UserCreatedEvent(savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), bonusToken, false));

        return jwtProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole().name());
    }

    @Override
    public Map<String, Object> signin(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        if (user.getPassword() == null) {
            throw new UnauthorizedException("This account was created via Google. Please log in with Google or set a password via profile.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        Map<String, Object> response = new HashMap<>();
        
        String jwt = jwtProvider.generateToken(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());

        if (user.isTwoFactorEnabled()) {
            TwoFactorOTP twoFactorOTP = twoFactorOtpService.createOtp(user.getId(), user.getEmail(), user.getFullName(), jwt);
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
    public Map<String, Object> verifySigninOtp(String otp, String sessionId) {
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(sessionId)
                .orElseThrow(() -> new InvalidOtpException("Invalid session"));

        if (!twoFactorOtpService.verifyOtp(twoFactorOTP, otp)) {
            throw new InvalidOtpException("Invalid or expired OTP");
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id=" + userId));

        if (user.isVerified()) {
            log.info(LogUtil.info(
                "auth-service",
                "sendVerificationCode",
                userId.toString(),
                "User already verified — skipping"
            ));
            return;
        }

        VerificationCode code = getOrCreateOtp(userId, email, type);
        kafkaTemplate.send(otpNotificationTopic, new OtpNotificationEvent(userId, email, user.getFullName(), code.getOtp(), type, type.name(), null));
        
        log.info(LogUtil.info(
            "auth-service",
            "sendVerificationCode",
            userId.toString(),
            "OTP event published for type=" + type
        ));
    }

    private VerificationCode getOrCreateOtp(Long userId, String email, VerificationType type) {
        Optional<VerificationCode> existing = verificationCodeRepository.findByUserIdAndVerificationType(userId, type);

        if (existing.isPresent()) {
            VerificationCode code = existing.get();
            // If OTP has more than 2 minutes left, reuse it
            if (code.getExpiryTime() != null &&
                LocalDateTime.now().isBefore(code.getExpiryTime().minusMinutes(2))) {
                
                log.info(LogUtil.info(
                    "auth-service",
                    "getOrCreateOtp",
                    userId.toString(),
                    "Reusing existing valid OTP for type=" + type
                ));
                return code;
            }
            verificationCodeRepository.delete(code);
            log.info(LogUtil.info(
                "auth-service",
                "getOrCreateOtp",
                userId.toString(),
                "Deleted expired/near-expiry OTP, creating new for type=" + type
            ));
        }

        String otp = OtpUtils.generateOTP();
        VerificationCode newCode = new VerificationCode();
        newCode.setUserId(userId);
        newCode.setEmail(email);
        newCode.setOtp(otp);
        newCode.setVerificationType(type);
        newCode.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        
        return verificationCodeRepository.save(newCode);
    }

    @Override
    @Transactional
    public void verifyEmail(Long userId, String otp) {
        VerificationCode code = verificationCodeRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidOtpException("Verification code not found"));

        if (code.getExpiryTime().isBefore(LocalDateTime.now()) || !code.getOtp().equals(otp)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));
        user.setVerified(true);
        
        // Mark email as verified
        // Check KYC status
        checkAndUpdateKycStatus(user);
        
        // Send bonus claim email if not yet availed
        if (!user.isSignupBonusAvailed() && user.getBonusClaimToken() != null) {
            // Publish event to send claim email
            OtpNotificationEvent bonusEvent = new OtpNotificationEvent();
            bonusEvent.setUserId(user.getId());
            bonusEvent.setEmail(user.getEmail());
            bonusEvent.setFullName(user.getFullName());
            bonusEvent.setBonusClaimToken(user.getBonusClaimToken());
            bonusEvent.setEventType("CLAIM_BONUS_EMAIL");
            
            kafkaTemplate.send(otpNotificationTopic, bonusEvent);
            
            log.info(LogUtil.info(
                "auth-service",
                "verifyEmailOtp",
                user.getId().toString(),
                "Bonus claim email event published"
            ));
        }
        
        userRepository.save(user);
        verificationCodeRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public String sendForgotPasswordOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        ForgotPasswordToken token = getOrCreateForgotPasswordToken(user.getId(), email);
        
        kafkaTemplate.send(otpNotificationTopic, new OtpNotificationEvent(user.getId(), email, user.getFullName(), token.getOtp(), VerificationType.FORGOT_PASSWORD, "FORGOT_PASSWORD", null));
        return token.getId();
    }

    private ForgotPasswordToken getOrCreateForgotPasswordToken(Long userId, String email) {
        Optional<ForgotPasswordToken> existing = forgotPasswordTokenRepository.findByUserId(userId);

        if (existing.isPresent()) {
            ForgotPasswordToken token = existing.get();
            // If OTP has more than 2 minutes left, reuse it
            if (token.getExpiryTime() != null &&
                LocalDateTime.now().isBefore(token.getExpiryTime().minusMinutes(2))) {
                
                log.info(LogUtil.info(
                    "auth-service",
                    "getOrCreateForgotPasswordToken",
                    userId.toString(),
                    "Reusing existing valid ForgotPassword token"
                ));
                return token;
            }
            forgotPasswordTokenRepository.deleteByUserId(userId);
            log.info(LogUtil.info(
                "auth-service",
                "getOrCreateForgotPasswordToken",
                userId.toString(),
                "Deleted expired/near-expiry ForgotPassword token"
            ));
        }

        String code = OtpUtils.generateOTP();
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setId(java.util.UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setEmail(email);
        token.setOtp(code);
        token.setVerificationType(VerificationType.FORGOT_PASSWORD);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(10));

        return forgotPasswordTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        ForgotPasswordToken token = forgotPasswordTokenRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired OTP"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP has expired");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(token.getUserId())));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        forgotPasswordTokenRepository.delete(token);
    }

    @Override
    public void updateTwoFactorStatus(Long userId, boolean status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));
        user.setTwoFactorEnabled(status);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void setPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));

        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getPinCode() != null) user.setPinCode(request.getPinCode());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());

        // Mark email as verified
        // Check KYC status
        checkAndUpdateKycStatus(user);

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public Map<String, Object> getKycStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));

        Map<String, Object> status = new HashMap<>();
        status.put("kycStatus", user.getKycStatus());
        status.put("isEmailVerified", user.isVerified());
        
        boolean hasPhone = user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank();
        boolean hasAddress = user.getAddress() != null && !user.getAddress().isBlank();
        
        status.put("hasPhone", hasPhone);
        status.put("hasAddress", hasAddress);
        
        List<String> missingFields = new ArrayList<>();
        if (!user.isVerified()) missingFields.add("Email Verification");
        if (!hasPhone) missingFields.add("Phone Number");
        if (!hasAddress) missingFields.add("Full Address");
        if (user.getCity() == null) missingFields.add("City");
        if (user.getCountry() == null) missingFields.add("Country");
        
        status.put("missingFields", missingFields);
        status.put("canDeposit", user.getKycStatus() == KycStatus.VERIFIED);
        
        return status;
    }

    @Override
    @Transactional
    public WalletDTO claimSignupBonus(String token) {
        // 1. Find user by token
        User user = userRepository.findByBonusClaimToken(token)
            .orElseThrow(() -> new BusinessException("Invalid or expired bonus claim link."));

        // 2. Double-check already claimed (CRITICAL)
        if (user.isSignupBonusAvailed()) {
            log.warn(LogUtil.warn("auth-service", "claimSignupBonus", user.getId().toString(), "User already claimed bonus, skipping Feign call"));
            throw new BusinessException("Bonus has already been claimed for this account.");
        }

        // 3. Check token expiry
        if (LocalDateTime.now().isAfter(user.getBonusClaimTokenExpiry())) {
            throw new BusinessException("This bonus claim link has expired. Please request a new one from your profile.");
        }

        // 4. Check email verified
        if (!user.isVerified()) {
            throw new BusinessException("Please verify your email before claiming the bonus.");
        }

        // 5. Credit bonus via Feign call
        try {
            WalletDTO wallet = creditBonusViaFeign(user.getId());

            // 6. Mark bonus as availed (COMMIT)
            user.setSignupBonusAvailed(true);
            user.setBonusClaimToken(null);
            user.setBonusClaimTokenExpiry(null);
            userRepository.save(user);

            log.info(LogUtil.info(
                "auth-service",
                "POST /auth/claim-bonus",
                user.getId().toString(),
                "Signup bonus marked as availed in auth-service"
            ));

            return wallet;
        } catch (Exception e) {
            log.error(LogUtil.error(
                "auth-service",
                "POST /auth/claim-bonus",
                user.getId().toString(),
                "Bonus credit failed: " + e.getMessage()
            ));
            throw new ExternalServiceException("core-trading-service", "Could not credit bonus. Please try again.");
        }
    }

    private WalletDTO creditBonusViaFeign(Long userId) {
        ApiResponse<WalletDTO> response = coreTradingClient.creditSignupBonus(userId);
        if (response == null || !response.isSuccess()) {
            throw new ExternalServiceException("core-trading-service", "Bonus credit failed");
        }
        return response.getData();
    }

    private void checkAndUpdateKycStatus(User user) {
        boolean emailVerified = user.isVerified();
        boolean hasPhone = user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank();
        boolean hasAddress = user.getAddress() != null && !user.getAddress().isBlank()
                && user.getCity() != null && !user.getCity().isBlank()
                && user.getCountry() != null && !user.getCountry().isBlank();

        if (emailVerified && hasPhone && hasAddress && user.getKycStatus() != KycStatus.VERIFIED) {
            user.setKycStatus(KycStatus.VERIFIED);
            user.setKycVerifiedAt(LocalDateTime.now());
            log.info(LogUtil.info("auth-service", "KYC", user.getId().toString(), "KYC status changed to VERIFIED"));
        }
    }

    @Override
    @Transactional
    public void resendBonusLink(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", String.valueOf(userId)));

        if (user.isSignupBonusAvailed()) {
            throw new BusinessException("Bonus has already been claimed.");
        }

        // Generate new token
        String newToken = BonusTokenUtils.generateToken();
        user.setBonusClaimToken(newToken);
        user.setBonusClaimTokenExpiry(BonusTokenUtils.getExpiry());
        userRepository.save(user);

        // Send via Kafka
        kafkaTemplate.send(otpNotificationTopic, new OtpNotificationEvent(
            user.getId(), 
            user.getEmail(), 
            user.getFullName(), 
            null, 
            null, 
            "CLAIM_BONUS_EMAIL", 
            newToken
        ));

        log.info(LogUtil.info(
            "auth-service",
            "resendBonusLink",
            userId.toString(),
            "New bonus claim link sent to: " + maskEmail(user.getEmail())
        ));
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        String masked = local.length() > 3 ? local.substring(0, 3) + "***" : "***";
        return masked + "@" + domain;
    }
}
