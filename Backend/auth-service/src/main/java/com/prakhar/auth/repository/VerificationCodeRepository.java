package com.prakhar.auth.repository;

import com.prakhar.auth.entity.VerificationCode;
import com.prakhar.common.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserId(Long userId);
    Optional<VerificationCode> findByUserIdAndVerificationType(Long userId, VerificationType type);
    void deleteByUserId(Long userId);
}
