package com.prakhar.coretrading.service.impl;

import com.prakhar.common.dto.WithdrawalDTO;
import com.prakhar.common.enums.WithdrawalStatus;
import com.prakhar.common.exception.*;
import com.prakhar.common.event.WithdrawalStatusEvent;
import com.prakhar.coretrading.utils.RequestContext;
import com.prakhar.coretrading.entity.Wallet;
import com.prakhar.coretrading.entity.Withdrawal;
import com.prakhar.coretrading.mapper.TradingMapper;
import com.prakhar.coretrading.repository.PaymentDetailsRepository;
import com.prakhar.coretrading.repository.WalletRepository;
import com.prakhar.coretrading.repository.WithdrawalRepository;
import com.prakhar.coretrading.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final WalletRepository walletRepository;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final TradingMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.withdrawal:withdrawal-status}")
    private String withdrawalTopic;

    public WithdrawalServiceImpl(WithdrawalRepository withdrawalRepository, 
                                 WalletRepository walletRepository,
                                 PaymentDetailsRepository paymentDetailsRepository,
                                 TradingMapper mapper,
                                 KafkaTemplate<String, Object> kafkaTemplate) {
        this.withdrawalRepository = withdrawalRepository;
        this.walletRepository = walletRepository;
        this.paymentDetailsRepository = paymentDetailsRepository;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public WithdrawalDTO requestWithdrawal(Long userId, BigDecimal amount) {
        // 1. Check if payment details exist
        if (paymentDetailsRepository.findByUserId(userId).isEmpty()) {
            throw new BusinessException("Please add bank/UPI payment details before requesting a withdrawal");
        }

        // 2. Check for pending withdrawals
        if (withdrawalRepository.existsByUserIdAndStatus(userId, WithdrawalStatus.PENDING)) {
            throw new BusinessException("You already have a pending withdrawal request. Please wait for it to be processed.");
        }

        // 3. Check wallet balance
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId=" + userId));
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(amount, wallet.getBalance());
        }

        // 4. Deduct balance (Locking should ideally be used here if concurrency is high)
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(userId);
        withdrawal.setEmail(RequestContext.getUserEmail());
        withdrawal.setAmount(amount);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setDate(LocalDateTime.now());
        
        Withdrawal saved = withdrawalRepository.save(withdrawal);

        kafkaTemplate.send(withdrawalTopic, new WithdrawalStatusEvent(
                saved.getId(), userId, saved.getEmail(), RequestContext.getUserFullName(), amount, "PENDING"
        ));

        return mapper.toWithdrawalDTO(saved);
    }

    @Override
    @Transactional
    public WithdrawalDTO processWithdrawal(Long withdrawalId, boolean accept) {
        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new ResourceNotFoundException("Withdrawal", "id=" + withdrawalId));

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new BusinessException("Withdrawal has already been " + withdrawal.getStatus().name().toLowerCase());
        }

        if (accept) {
            withdrawal.setStatus(WithdrawalStatus.SUCCESS);
        } else {
            // If declined, refund the amount to wallet
            Wallet wallet = walletRepository.findByUserId(withdrawal.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId=" + withdrawal.getUserId()));
            wallet.setBalance(wallet.getBalance().add(withdrawal.getAmount()));
            walletRepository.save(wallet);
            
            withdrawal.setStatus(WithdrawalStatus.DECLINE);
        }
        withdrawal.setDate(LocalDateTime.now());
        
        Withdrawal saved = withdrawalRepository.save(withdrawal);

        // Note: For processWithdrawal (Admin action), RequestContext might not have user details of the requester.
        // In a real app, we'd fetch the user's name from User service or store it in Withdrawal entity.
        // For now, using email as a fallback for name if fullName is missing in event.
        kafkaTemplate.send(withdrawalTopic, new WithdrawalStatusEvent(
                saved.getId(), saved.getUserId(), saved.getEmail(), saved.getEmail(), saved.getAmount(), saved.getStatus().name()
        ));

        return mapper.toWithdrawalDTO(saved);
    }

    @Override
    public List<WithdrawalDTO> getUsersWithdrawalHistory(Long userId) {
        return withdrawalRepository.findByUserIdOrderByDateDesc(userId)
                .stream().map(mapper::toWithdrawalDTO).collect(Collectors.toList());
    }

    @Override
    public List<WithdrawalDTO> getAllWithdrawalRequests() {
        return withdrawalRepository.findAllByOrderByDateDesc()
                .stream().map(mapper::toWithdrawalDTO).collect(Collectors.toList());
    }
}
