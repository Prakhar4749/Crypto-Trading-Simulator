package com.prakhar.marketai.service.impl;

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
    public Object getCoinList(int page, String currency) {
        if (page < 1) {
            throw new BusinessException("Page number must be at least 1");
        }
        String cacheKey = "coin_list_" + page + "_" + currency;
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return cachedData;

        String url = coingeckoBaseUrl + "/coins/markets?vs_currency=" + currency + "&per_page=10&page=" + page;
        Object response = fetchFromCoinGecko(url, "list", Object.class);
        redisTemplate.opsForValue().set(cacheKey, response, 5, TimeUnit.MINUTES);
        return response;
    }

    @Override
    public Object getTop50Coins() {
        String cacheKey = "top50";
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return cachedData;

        String url = coingeckoBaseUrl + "/coins/markets?vs_currency=usd&per_page=50&page=1";
        Object response = fetchFromCoinGecko(url, "top50", Object.class);
        redisTemplate.opsForValue().set(cacheKey, response, 10, TimeUnit.MINUTES);
        return response;
    }

    @Override
    public Object searchCoins(String query) {
        if (query == null || query.isBlank()) {
            throw new BusinessException("Search query cannot be empty");
        }
        String url = coingeckoBaseUrl + "/search?query=" + query;
        return fetchFromCoinGecko(url, "search", Object.class);
    }

    @Override
    public Object getCoinChart(String coinId, int days, String currency) {
        if (coinId == null || coinId.isBlank()) {
            throw new BusinessException("Coin ID cannot be empty");
        }
        String cacheKey = "chart_" + coinId + "_" + days + "_" + currency;
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return cachedData;

        String url = coingeckoBaseUrl + "/coins/" + coinId + "/market_chart?vs_currency=" + currency + "&days=" + days;
        Object response = fetchFromCoinGecko(url, coinId, Object.class);
        redisTemplate.opsForValue().set(cacheKey, response, 5, TimeUnit.MINUTES);
        return response;
    }

    @Override
    public Object getCoinById(String coinId) {
        if (coinId == null || coinId.isBlank()) {
            throw new BusinessException("Coin ID cannot be empty");
        }
        String cacheKey = "coin_details_" + coinId;
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return cachedData;

        String url = coingeckoBaseUrl + "/coins/" + coinId;
        Object response = fetchFromCoinGecko(url, coinId, Object.class);
        redisTemplate.opsForValue().set(cacheKey, response, 10, TimeUnit.MINUTES);
        return response;
    }

    @Override
    public Object getCoinsByIds(java.util.List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        String idsParam = String.join(",", ids);
        String url = coingeckoBaseUrl + "/coins/markets?vs_currency=usd&ids=" + idsParam;
        return fetchFromCoinGecko(url, "bulk", Object.class);
    }

    @Override
    public Object getTrendingCoins() {
        String cacheKey = "trending";
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) return cachedData;

        String url = coingeckoBaseUrl + "/search/trending";
        Object response = fetchFromCoinGecko(url, "trending", Object.class);
        redisTemplate.opsForValue().set(cacheKey, response, 10, TimeUnit.MINUTES);
        return response;
    }

    @Override
    public String chat(String message, String userId) {
        if (message == null || message.isBlank()) {
            throw new BusinessException("Chat message cannot be empty");
        }
        String url = geminiApiUrl + (geminiApiUrl.contains("?") ? "&" : "?") + "key=" + geminiApiKey;
        
        JSONObject requestBody = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject contentsObject = new JSONObject();
        JSONArray partsArray = new JSONArray();
        partsArray.put(new JSONObject().put("text", message));
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

    private <T> T fetchFromCoinGecko(String url, String coinId, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (coingeckoApiKey != null && !coingeckoApiKey.isBlank()) {
            headers.set("x-cg-demo-api-key", coingeckoApiKey);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
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
