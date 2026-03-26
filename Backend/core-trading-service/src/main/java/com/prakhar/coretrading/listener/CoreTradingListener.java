package com.prakhar.coretrading.listener;

import com.prakhar.common.enums.WalletTransactionType;
import com.prakhar.coretrading.entity.Wallet;
import com.prakhar.coretrading.entity.WalletTransaction;
import com.prakhar.coretrading.repository.WalletRepository;
import com.prakhar.coretrading.repository.WalletTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class CoreTradingListener {

    private static final Logger logger = LoggerFactory.getLogger(CoreTradingListener.class);
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Value("${wallet.signup-bonus}")
    private BigDecimal signupBonus;

    public CoreTradingListener(WalletRepository walletRepository, 
                               WalletTransactionRepository walletTransactionRepository) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @KafkaListener(topics = "user-created", groupId = "core-trading-group")
    @Transactional
    public void onUserCreated(Map<String, Object> event) {
        Long userId = Long.valueOf(String.valueOf(event.get("userId")));
        
        logger.info("Creating wallet for userId={} with signup bonus={}", userId, signupBonus);
        
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(signupBonus);
        
        Wallet savedWallet = walletRepository.save(wallet);
        
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(savedWallet.getId());
        transaction.setType(WalletTransactionType.SIGNUP_BONUS);
        transaction.setDate(LocalDateTime.now());
        transaction.setPurpose("Signup Bonus");
        transaction.setAmount(signupBonus);
        
        walletTransactionRepository.save(transaction);
        logger.info("New wallet created for userId={} with signup bonus={}", userId, signupBonus);
    }
}
