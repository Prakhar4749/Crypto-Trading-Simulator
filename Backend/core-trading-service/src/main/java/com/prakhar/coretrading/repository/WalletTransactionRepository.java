package com.prakhar.coretrading.repository;

import com.prakhar.common.enums.WalletTransactionType;
import com.prakhar.coretrading.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletIdOrderByDateDesc(Long walletId);
    boolean existsByWalletIdAndType(Long walletId, WalletTransactionType type);
}
