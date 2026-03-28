package com.prakhar.coretrading.listener;

import com.prakhar.common.event.UserCreatedEvent;
import com.prakhar.common.util.LogUtil;
import com.prakhar.coretrading.repository.WalletRepository;
import com.prakhar.coretrading.service.CoreTradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CoreTradingListener {

    private static final Logger logger = LoggerFactory.getLogger(CoreTradingListener.class);
    private final WalletRepository walletRepository;
    private final CoreTradingService coreTradingService;

    @Value("${kafka.topic.user-created:user-created}")
    private String userCreatedTopic;

    public CoreTradingListener(WalletRepository walletRepository, 
                               CoreTradingService coreTradingService) {
        this.walletRepository = walletRepository;
        this.coreTradingService = coreTradingService;
    }

    @KafkaListener(topics = "${kafka.topic.user-created:user-created}", groupId = "core-trading-group")
    public void onUserCreated(UserCreatedEvent event) {
        // Safety net: create wallet if not exists
        // This handles edge cases where Feign succeeded but was not committed
        if (!walletRepository.existsByUserId(event.getUserId())) {
            logger.warn(LogUtil.warn(
                "core-trading-service",
                "Kafka:user-created",
                event.getUserId().toString(),
                "Wallet missing — creating via safety net"
            ));
            coreTradingService.createWalletForUser(
                event.getUserId(),
                event.getEmail(),
                event.getFullName()
            );
        }
    }
}
