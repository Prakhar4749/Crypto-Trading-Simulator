package com.prakhar.coretrading.mapper;

import com.prakhar.common.dto.*;
import com.prakhar.coretrading.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TradingMapper {

    public WalletDTO toWalletDTO(Wallet wallet) {
        if (wallet == null) return null;
        WalletDTO dto = new WalletDTO();
        dto.setId(wallet.getId());
        dto.setUserId(wallet.getUserId());
        dto.setBalance(wallet.getBalance());
        return dto;
    }

    public WalletTransactionDTO toWalletTransactionDTO(WalletTransaction transaction) {
        if (transaction == null) return null;
        WalletTransactionDTO dto = new WalletTransactionDTO();
        dto.setId(transaction.getId());
        dto.setWalletId(transaction.getWalletId());
        dto.setType(transaction.getType());
        dto.setDate(transaction.getDate());
        dto.setTransferId(transaction.getTransferId());
        dto.setPurpose(transaction.getPurpose());
        dto.setAmount(transaction.getAmount());
        return dto;
    }

    public PaymentDetailsDTO toPaymentDetailsDTO(PaymentDetails details) {
        if (details == null) return null;
        PaymentDetailsDTO dto = new PaymentDetailsDTO();
        dto.setId(details.getId());
        dto.setAccountNumber(details.getAccountNumber());
        dto.setAccountHolderName(details.getAccountHolderName());
        dto.setIfsc(details.getIfsc());
        dto.setBankName(details.getBankName());
        dto.setUserId(details.getUserId());
        return dto;
    }

    public AssetDTO toAssetDTO(Asset asset) {
        if (asset == null) return null;
        AssetDTO dto = new AssetDTO();
        dto.setId(asset.getId());
        dto.setUserId(asset.getUserId());
        dto.setCoinId(asset.getCoinId());
        dto.setQuantity(asset.getQuantity());
        dto.setBuyPrice(asset.getBuyPrice());
        return dto;
    }

    public OrderDTO toOrderDTO(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setOrderType(order.getOrderType());
        dto.setPrice(order.getPrice());
        dto.setTimestamp(order.getTimestamp());
        dto.setStatus(order.getStatus());
        dto.setCoinId(order.getCoinId());
        dto.setQuantity(order.getQuantity());
        dto.setBuyPrice(order.getBuyPrice());
        dto.setSellPrice(order.getSellPrice());
        return dto;
    }

    public WithdrawalDTO toWithdrawalDTO(Withdrawal withdrawal) {
        if (withdrawal == null) return null;
        WithdrawalDTO dto = new WithdrawalDTO();
        dto.setId(withdrawal.getId());
        dto.setUserId(withdrawal.getUserId());
        dto.setEmail(withdrawal.getEmail());
        dto.setAmount(withdrawal.getAmount());
        dto.setStatus(withdrawal.getStatus());
        dto.setDate(withdrawal.getDate());
        return dto;
    }
}
