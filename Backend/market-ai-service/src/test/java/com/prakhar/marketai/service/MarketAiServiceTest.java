package com.prakhar.marketai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakhar.common.exception.BusinessException;
import com.prakhar.common.exception.ResourceNotFoundException;
import com.prakhar.marketai.service.impl.MarketAiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MarketAiService Unit Tests")
class MarketAiServiceTest {

  @Mock private RedisTemplate<String, Object> redisTemplate;
  @Mock private ValueOperations<String, Object> valueOperations;
  @Mock private ObjectMapper objectMapper;
  @Mock private RestTemplate restTemplate;

  @InjectMocks
  private MarketAiServiceImpl marketAiService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(marketAiService, "restTemplate", restTemplate);
    ReflectionTestUtils.setField(marketAiService, "coingeckoBaseUrl", "https://api.coingecko.com/api/v3");
    ReflectionTestUtils.setField(marketAiService, "geminiApiUrl", "https://api.gemini.com");
    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  @DisplayName("Get coin details — success from API")
  void getCoinDetailsSuccess() throws Exception {
    when(valueOperations.get(anyString())).thenReturn(null);
    String jsonResponse = "{\"id\":\"bitcoin\",\"name\":\"Bitcoin\"}";
    ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
      .thenReturn(responseEntity);
    when(objectMapper.readTree(anyString())).thenReturn(mock(JsonNode.class));

    JsonNode result = marketAiService.getCoinDetails("bitcoin");
    assertNotNull(result);
    verify(valueOperations).set(eq("coin_details_bitcoin"), eq(jsonResponse), anyLong(), any());
  }

  @Test
  @DisplayName("Get coin details — from cache")
  void getCoinDetailsFromCache() throws Exception {
    when(valueOperations.get("coin_details_bitcoin")).thenReturn("cached-json");
    when(objectMapper.readTree("cached-json")).thenReturn(mock(JsonNode.class));

    JsonNode result = marketAiService.getCoinDetails("bitcoin");
    assertNotNull(result);
    verify(restTemplate, never()).exchange(anyString(), any(), any(), eq(String.class));
  }

  @Test
  @DisplayName("Get coin details — invalid ID 404")
  void getCoinDetailsNotFound() {
    when(valueOperations.get(anyString())).thenReturn(null);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
      .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    assertThrows(ResourceNotFoundException.class, () -> marketAiService.getCoinDetails("invalid-id"));
  }

  @Test
  @DisplayName("Search coins — empty query throws BusinessException")
  void searchCoinsEmptyQuery() {
    assertThrows(BusinessException.class, () -> marketAiService.searchCoin(""));
  }

  @Test
  @DisplayName("Get AI Response — empty prompt throws BusinessException")
  void getAiResponseEmptyPrompt() {
    assertThrows(BusinessException.class, () -> marketAiService.getAiResponse(" "));
  }
}
