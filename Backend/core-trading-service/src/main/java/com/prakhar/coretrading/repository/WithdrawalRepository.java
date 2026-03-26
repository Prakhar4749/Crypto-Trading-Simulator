package com.prakhar.coretrading.repository;

import com.prakhar.common.enums.WithdrawalStatus;
import com.prakhar.coretrading.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findByUserIdOrderByDateDesc(Long userId);
    List<Withdrawal> findAllByOrderByDateDesc();
    boolean existsByUserIdAndStatus(Long userId, WithdrawalStatus status);
}
