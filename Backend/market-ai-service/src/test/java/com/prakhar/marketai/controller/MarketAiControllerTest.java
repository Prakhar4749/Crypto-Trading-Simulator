package com.prakhar.marketai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakhar.marketai.dto.request.ChatRequest;
import com.prakhar.marketai.service.MarketAiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarketAiController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MarketAiController Tests")
class MarketAiControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private MarketAiService marketAiService;

  @Test
  @DisplayName("GET /api/coins — 200 OK")
  void getCoinsReturns200() throws Exception {
    when(marketAiService.getCoinList(anyInt(), anyString())).thenReturn(Map.of("id", "bitcoin"));

    mockMvc.perform(get("/api/coins")
        .param("page", "1"))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /api/coins/{id} — 200 OK")
  void getCoinDetailsReturns200() throws Exception {
    when(marketAiService.getCoinById("bitcoin")).thenReturn(Map.of("id", "bitcoin"));

    mockMvc.perform(get("/api/coins/bitcoin"))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /api/market/search — 200 OK")
  void searchCoinReturns200() throws Exception {
    when(marketAiService.searchCoins(anyString())).thenReturn(Map.of("id", "bitcoin"));

    mockMvc.perform(get("/api/market/search")
        .param("query", "btc"))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /api/chat/bot — 200 OK")
  void chatBotReturns200() throws Exception {
    ChatRequest req = new ChatRequest();
    req.setMessage("What is Bitcoin?");

    when(marketAiService.chat(anyString(), any())).thenReturn("Bitcoin is a cryptocurrency...");

    mockMvc.perform(post("/api/chat/bot")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("AI response"))
      .andExpect(jsonPath("$.data").value("Bitcoin is a cryptocurrency..."));
  }

  @Test
  @DisplayName("POST /api/chat/bot — 400 for empty message")
  void chatBotEmptyPromptReturns400() throws Exception {
    ChatRequest req = new ChatRequest();
    req.setMessage("");

    mockMvc.perform(post("/api/chat/bot")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
      .andExpect(status().isBadRequest());
  }
}
