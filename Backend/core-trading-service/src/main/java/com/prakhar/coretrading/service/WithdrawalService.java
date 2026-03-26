package com.prakhar.coretrading.service;

import com.prakhar.common.dto.WithdrawalDTO;

import java.math.BigDecimal;
import java.util.List;

public interface WithdrawalService {
    WithdrawalDTO requestWithdrawal(Long userId, BigDecimal amount);
    WithdrawalDTO processWithdrawal(Long withdrawalId, boolean accept) throws Exception;
    List<WithdrawalDTO> getUsersWithdrawalHistory(Long userId);
    List<WithdrawalDTO> getAllWithdrawalRequests();
}
