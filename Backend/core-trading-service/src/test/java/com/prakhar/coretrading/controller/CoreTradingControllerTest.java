package com.prakhar.coretrading.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.WalletDTO;
import com.prakhar.common.exception.InsufficientBalanceException;
import com.prakhar.coretrading.dto.request.TransferRequest;
import com.prakhar.coretrading.entity.Wallet;
import com.prakhar.coretrading.mapper.TradingMapper;
import com.prakhar.coretrading.repository.AssetRepository;
import com.prakhar.coretrading.repository.OrderRepository;
import com.prakhar.coretrading.repository.WalletRepository;
import com.prakhar.coretrading.repository.WatchlistCoinRepository;
import com.prakhar.coretrading.service.CoreTradingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CoreTradingController.class, InternalController.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CoreTrading Controllers Tests")
class CoreTradingControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean private CoreTradingService service;
  @MockBean private WalletRepository walletRepository;
  @MockBean private OrderRepository orderRepository;
  @MockBean private AssetRepository assetRepository;
  @MockBean private WatchlistCoinRepository watchlistCoinRepository;
  @MockBean private com.prakhar.coretrading.feign.MarketAiClient marketAiClient;
  @MockBean private TradingMapper mapper;

  @Test
  @DisplayName("GET /api/wallet — success")
  void getWalletSuccess() throws Exception {
    Wallet wallet = new Wallet();
    wallet.setUserId(1L);
    when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
    when(mapper.toWalletDTO(any())).thenReturn(new WalletDTO());

    mockMvc.perform(get("/api/wallet")
        .header("X-User-ID", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("PUT /api/wallet/transfer — success")
  void transferSuccess() throws Exception {
    TransferRequest req = new TransferRequest();
    req.setReceiverWalletId(2L);
    req.setAmount(new BigDecimal("100"));
    req.setPurpose("gift");

    when(service.transferToWallet(anyLong(), anyLong(), any(), any()))
      .thenReturn(new WalletDTO());

    mockMvc.perform(put("/api/wallet/transfer")
        .header("X-User-ID", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("PUT /api/wallet/transfer — insufficient balance 422")
  void transferInsufficientBalance() throws Exception {
    TransferRequest req = new TransferRequest();
    req.setReceiverWalletId(2L);
    req.setAmount(new BigDecimal("1000"));

    when(service.transferToWallet(anyLong(), anyLong(), any(), any()))
      .thenThrow(new InsufficientBalanceException(new BigDecimal("1000"), new BigDecimal("100")));

    mockMvc.perform(put("/api/wallet/transfer")
        .header("X-User-ID", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
      .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @DisplayName("POST /internal/wallet/create — success 201")
  void createWalletSuccess() throws Exception {
    when(service.createWalletForUser(anyLong(), anyString(), anyString()))
      .thenReturn(new WalletDTO());

    String body = "{\"userId\":1,\"email\":\"test@test.com\",\"fullName\":\"Test\"}";

    mockMvc.perform(post("/internal/wallet/create")
        .header("X-Internal-Api-Key", "test-key")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.success").value(true));
  }
}
