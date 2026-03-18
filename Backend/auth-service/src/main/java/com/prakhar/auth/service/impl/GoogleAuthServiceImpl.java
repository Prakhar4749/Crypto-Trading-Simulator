package com.prakhar.auth.service.impl;

import com.prakhar.auth.entity.User;
import com.prakhar.auth.repository.UserRepository;
import com.prakhar.auth.service.GoogleAuthService;
import com.prakhar.auth.utils.JwtProvider;
import com.prakhar.common.event.UserCreatedEvent;
import com.prakhar.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public GoogleAuthServiceImpl(UserRepository userRepository, JwtProvider jwtProvider, 
                                 RestTemplate restTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public Map<String, Object> authenticateWithGoogle(String googleIdToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + googleIdToken;
        Map<String, Object> googleResponse = restTemplate.getForObject(url, Map.class);

        if (googleResponse == null || googleResponse.containsKey("error_description")) {
            throw new UnauthorizedException("Invalid Google ID Token");
        }

        String email = (String) googleResponse.get("email");
        String name = (String) googleResponse.get("name");

        logger.info("Google OAuth2 login for email={}", maskEmail(email));

        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;

        if (userOpt.isEmpty()) {
            user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setMobile("0000000000");
            user.setRole("ROLE_USER");
            user.setVerified(true);
            user.setTwoFactorEnabled(false);
            user = userRepository.save(user);
            
            kafkaTemplate.send("user-created", new UserCreatedEvent(user.getId(), email, name));
            logger.info("New user created via Google OAuth2: {}", maskEmail(email));
        } else {
            user = userOpt.get();
        }

        String jwt = jwtProvider.generateToken(user.getId(), email, user.getRole());

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
