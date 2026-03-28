package com.prakhar.coretrading.service;

import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.UserDTO;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.exception.BusinessException;
import com.prakhar.common.exception.InsufficientBalanceException;
import com.prakhar.common.exception.ResourceNotFoundException;
import com.prakhar.coretrading.config.RazorpayConfig;
import com.prakhar.coretrading.entity.Wallet;
import com.prakhar.coretrading.feign.AuthClient;
import com.prakhar.coretrading.feign.MarketAiClient;
import com.prakhar.coretrading.mapper.TradingMapper;
import com.prakhar.coretrading.repository.*;
import com.prakhar.coretrading.service.impl.CoreTradingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoreTradingService Unit Tests")
class CoreTradingServiceTest {

  @Mock private WalletRepository walletRepository;
  @Mock private WalletTransactionRepository walletTransactionRepository;
  @Mock private AssetRepository assetRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private PaymentDetailsRepository paymentDetailsRepository;
  @Mock private MarketAiClient marketClient;
  @Mock private AuthClient authClient;
  @Mock private RazorpayConfig razorpayConfig;
  @Mock private KafkaTemplate<String, Object> kafkaTemplate;
  @Mock private TradingMapper mapper;

  @InjectMocks
  private CoreTradingServiceImpl coreTradingService;

  @Test
  @DisplayName("Create wallet success")
  void createWalletSuccess() {
    when(walletRepository.existsByUserId(1L)).thenReturn(false);
    when(walletRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(mapper.toWalletDTO(any())).thenReturn(new WalletDTO());

    WalletDTO result = coreTradingService.createWalletForUser(1L, "test@test.com", "Test User");
    assertNotNull(result);
    verify(walletRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Create wallet already exists")
  void createWalletAlreadyExists() {
    Wallet wallet = new Wallet();
    wallet.setUserId(1L);
    when(walletRepository.existsByUserId(1L)).thenReturn(true);
    when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
    when(mapper.toWalletDTO(any())).thenReturn(new WalletDTO());

    coreTradingService.createWalletForUser(1L, "test@test.com", "Test User");
    verify(walletRepository, never()).save(any());
  }

  @Test
  @DisplayName("Credit signup bonus success")
  void creditSignupBonusSuccess() {
    ReflectionTestUtils.setField(coreTradingService, "signupBonusAmount", "1000");
    Wallet wallet = new Wallet();
    wallet.setId(1L);
    wallet.setUserId(1L);
    wallet.setBalance(BigDecimal.ZERO);

    when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
    when(mapper.toWalletDTO(any())).thenReturn(new WalletDTO());

    coreTradingService.creditSignupBonus(1L);
    assertEquals(0, new BigDecimal("1000").compareTo(wallet.getBalance()));
    verify(walletTransactionRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Transfer to wallet — success")
  void transferSuccess() {
    Wallet sender = new Wallet();
    sender.setId(1L);
    sender.setUserId(1L);
    sender.setBalance(new BigDecimal("5000"));

    Wallet receiver = new Wallet();
    receiver.setId(2L);
    receiver.setUserId(2L);
    receiver.setBalance(BigDecimal.ZERO);

    when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(sender));
    when(walletRepository.findById(2L)).thenReturn(Optional.of(receiver));
    when(mapper.toWalletDTO(any())).thenReturn(new WalletDTO());

    coreTradingService.transferToWallet(1L, 2L, new BigDecimal("1000"), "test");

    assertEquals(0, new BigDecimal("4000").compareTo(sender.getBalance()));
    assertEquals(0, new BigDecimal("1000").compareTo(receiver.getBalance()));
  }

  @Test
  @DisplayName("Transfer — insufficient balance")
  void transferInsufficientBalance() {
    Wallet sender = new Wallet();
    sender.setId(1L);
    sender.setUserId(1L);
    sender.setBalance(new BigDecimal("500"));

    when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(sender));

    assertThrows(InsufficientBalanceException.class, 
      () -> coreTradingService.transferToWallet(1L, 2L, new BigDecimal("1000"), "test"));
  }

  @Test
  @DisplayName("Transfer — self transfer fails")
  void transferSelfFails() {
    Wallet sender = new Wallet();
    sender.setId(1L);
    sender.setUserId(1L);

    when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(sender));

    assertThrows(BusinessException.class, 
      () -> coreTradingService.transferToWallet(1L, 1L, new BigDecimal("100"), "test"));
  }
}
