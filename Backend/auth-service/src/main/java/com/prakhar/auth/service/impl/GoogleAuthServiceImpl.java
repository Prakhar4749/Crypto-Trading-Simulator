package com.prakhar.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.prakhar.auth.dto.request.CreateWalletRequest;
import com.prakhar.auth.entity.AuthProvider;
import com.prakhar.auth.entity.User;
import com.prakhar.auth.enums.UserRole;
import com.prakhar.auth.feign.CoreTradingClient;
import com.prakhar.auth.repository.UserRepository;
import com.prakhar.auth.service.GoogleAuthService;
import com.prakhar.auth.utils.BonusTokenUtils;
import com.prakhar.auth.utils.JwtProvider;
import com.prakhar.common.event.UserCreatedEvent;
import com.prakhar.common.exception.ExternalServiceException;
import com.prakhar.common.exception.UnauthorizedException;
import com.prakhar.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CoreTradingClient coreTradingClient;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${internal.api-key}")
    private String internalApiKey;

    @Value("${kafka.topic.user-created:user-created}")
    private String userCreatedTopic;

    public GoogleAuthServiceImpl(UserRepository userRepository, JwtProvider jwtProvider, 
                                 KafkaTemplate<String, Object> kafkaTemplate,
                                 CoreTradingClient coreTradingClient) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.kafkaTemplate = kafkaTemplate;
        this.coreTradingClient = coreTradingClient;
    }

    @Override
    @Transactional
    public Map<String, Object> authenticateWithGoogle(String googleIdToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(googleIdToken);
        } catch (Exception e) {
            logger.error("Error verifying Google ID Token: {}", e.getMessage());
            throw new UnauthorizedException("Failed to verify Google ID Token");
        }

        if (idToken == null) {
            throw new UnauthorizedException("Invalid Google ID Token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        logger.info("Google OAuth2 login for email={}", maskEmail(email));

        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;

        if (userOpt.isEmpty()) {
            // New user via Google
            user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setMobile("0000000000"); // Default or fetch if available
            user.setRole(UserRole.ROLE_USER);
            user.setVerified(true); // Google emails are already verified
            user.setTwoFactorEnabled(false);
            user.setAuthProvider(AuthProvider.GOOGLE);
            user = userRepository.save(user);
            
            // 2. Create wallet via Feign (SYNCHRONOUS)
            try {
                coreTradingClient.createWalletForUser(
                    new CreateWalletRequest(
                        user.getId(),
                        user.getEmail(),
                        user.getFullName()
                    )
                );
                logger.info(LogUtil.info(
                    "auth-service",
                    "GoogleAuth",
                    user.getId().toString(),
                    "Wallet created via Feign for: " + maskEmail(user.getEmail())
                ));
            } catch (Exception e) {
                logger.error(LogUtil.error(
                    "auth-service",
                    "GoogleAuth",
                    null,
                    "Wallet creation failed for Google user — rolling back: " + e.getMessage()
                ));
                userRepository.delete(user);
                throw new ExternalServiceException(
                    "core-trading-service",
                    "Could not initialize your account. Please try again."
                );
            }

            // Generate bonus claim token
            String bonusToken = BonusTokenUtils.generateToken();
            user.setBonusClaimToken(bonusToken);
            user.setBonusClaimTokenExpiry(BonusTokenUtils.getExpiry());
            user = userRepository.save(user);

            kafkaTemplate.send(userCreatedTopic, new UserCreatedEvent(user.getId(), email, name, bonusToken, true));
            logger.info("New user created via Google OAuth2: {}", maskEmail(email));
        } else {
            // Existing user - Robust Identity Mapping (Account Linking)
            user = userOpt.get();
            if (user.getAuthProvider() == AuthProvider.LOCAL) {
                logger.info("Linking existing local account to Google provider for email={}", maskEmail(email));
                user.setAuthProvider(AuthProvider.GOOGLE);
                user.setVerified(true);
                user = userRepository.save(user);
            }
        }

        String jwt = jwtProvider.generateToken(user.getId(), email, user.getFullName(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwt);
        response.put("message", "Google login success");
        return response;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) return "****" + email.substring(atIndex);
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }
}
