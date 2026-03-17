package com.prakhar.auth.repository;

import com.prakhar.auth.entity.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, String> {
    Optional<ForgotPasswordToken> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
