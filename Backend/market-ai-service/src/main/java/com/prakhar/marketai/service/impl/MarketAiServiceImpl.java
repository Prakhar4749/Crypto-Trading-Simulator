package com.prakhar.marketai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakhar.common.exception.BusinessException;
import com.prakhar.common.exception.ExternalServiceException;
import com.prakhar.common.exception.ResourceNotFoundException;
import com.prakhar.marketai.service.MarketAiService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class MarketAiServiceImpl implements MarketAiService {

    @Value("${coingecko.api.key}")
    private String coingeckoApiKey;

    @Value("${coingecko.base-url}")
    private String coingeckoBaseUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api-url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public MarketAiServiceImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode getCoinList(int page) throws Exception {
        if (page < 1) {
            throw new BusinessException("Page number must be at least 1");
        }
        String cacheKey = "coin_list_" + page;
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return objectMapper.readTree(cachedData.toString());

        String url = coingeckoBaseUrl + "/coins/markets?vs_currency=usd&per_page=10&page=" + page;
        String response = fetchFromCoinGecko(url, "list");
        redisTemplate.opsForValue().set(cacheKey, response, 5, TimeUnit.MINUTES);
        return objectMapper.readTree(response);
    }

    @Override
    public JsonNode getTop50() throws Exception {
        String cacheKey = "top50";
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return objectMapper.readTree(cachedData.toString());

        String url = coingeckoBaseUrl + "/coins/markets?vs_currency=usd&per_page=50&page=1";
        String response = fetchFromCoinGecko(url, "top50");
        redisTemplate.opsForValue().set(cacheKey, response, 10, TimeUnit.MINUTES);
        return objectMapper.readTree(response);
    }

    @Override
    public JsonNode searchCoin(String query) throws Exception {
        if (query == null || query.isBlank()) {
            throw new BusinessException("Search query cannot be empty");
        }
        String url = coingeckoBaseUrl + "/search?query=" + query;
        String response = fetchFromCoinGecko(url, "search");
        return objectMapper.readTree(response);
    }

    @Override
    public JsonNode getMarketChart(String coinId, int days) throws Exception {
        if (coinId == null || coinId.isBlank()) {
            throw new BusinessException("Coin ID cannot be empty");
        }
        String cacheKey = "chart_" + coinId + "_" + days;
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return objectMapper.readTree(cachedData.toString());

        String url = coingeckoBaseUrl + "/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days;
        String response = fetchFromCoinGecko(url, coinId);
        redisTemplate.opsForValue().set(cacheKey, response, 5, TimeUnit.MINUTES);
        return objectMapper.readTree(response);
    }

    @Override
    public JsonNode getCoinDetails(String coinId) throws Exception {
        if (coinId == null || coinId.isBlank()) {
            throw new BusinessException("Coin ID cannot be empty");
        }
        String cacheKey = "coin_details_" + coinId;
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return objectMapper.readTree(cachedData.toString());

        String url = coingeckoBaseUrl + "/coins/" + coinId;
        String response = fetchFromCoinGecko(url, coinId);
        redisTemplate.opsForValue().set(cacheKey, response, 10, TimeUnit.MINUTES);
        return objectMapper.readTree(response);
    }

    @Override
    public String getAiResponse(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new BusinessException("Chat message cannot be empty");
        }
        String url = geminiApiUrl + (geminiApiUrl.contains("?") ? "&" : "?") + "key=" + geminiApiKey;
        
        JSONObject requestBody = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject contentsObject = new JSONObject();
        JSONArray partsArray = new JSONArray();
        partsArray.put(new JSONObject().put("text", prompt));
        contentsObject.put("parts", partsArray);
        contentsArray.put(contentsObject);
        requestBody.put("contents", contentsArray);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("Gemini AI", "AI service error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ExternalServiceException("Gemini AI", "AI chatbot is temporarily unavailable");
        }
    }

    private String fetchFromCoinGecko(String url, String coinId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cg-demo-api-key", coingeckoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Coin", coinId);
            }
            throw new ExternalServiceException("CoinGecko", "Failed to fetch market data: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ExternalServiceException("CoinGecko", "CoinGecko service is currently unavailable");
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("CoinGecko", "Cannot reach CoinGecko. Check your internet connection.");
        }
    }
}
