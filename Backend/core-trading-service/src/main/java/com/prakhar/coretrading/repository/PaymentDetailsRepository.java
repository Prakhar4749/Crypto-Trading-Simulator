package com.prakhar.coretrading.repository;

import com.prakhar.coretrading.entity.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {
    Optional<PaymentDetails> findByUserId(Long userId);
}
