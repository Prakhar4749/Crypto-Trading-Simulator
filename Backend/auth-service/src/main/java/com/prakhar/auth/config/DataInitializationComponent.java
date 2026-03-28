package com.prakhar.auth.config;

import com.prakhar.auth.entity.AuthProvider;
import com.prakhar.auth.entity.User;
import com.prakhar.auth.enums.KycStatus;
import com.prakhar.auth.enums.UserRole;
import com.prakhar.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializationComponent implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationComponent.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.fullName:Admin}")
    private String adminFullName;

    public DataInitializationComponent(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setFullName(adminFullName);
            admin.setEmail(adminEmail);
            admin.setMobile("0000000000");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(UserRole.ROLE_ADMIN);
            admin.setKycStatus(KycStatus.VERIFIED);
            admin.setActive(true);
            admin.setVerified(true);
            admin.setSignupBonusAvailed(true);
            admin.setKycVerifiedAt(LocalDateTime.now());
            admin.setAuthProvider(AuthProvider.LOCAL);
            
            userRepository.save(admin);
            logger.info("Admin user initialized: {}", adminEmail);
        } else {
            logger.info("Admin user already present, skipping init");
        }
    }
}
