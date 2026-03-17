package com.prakhar.auth.repository;

import com.prakhar.auth.entity.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String> {
    Optional<TwoFactorOTP> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
