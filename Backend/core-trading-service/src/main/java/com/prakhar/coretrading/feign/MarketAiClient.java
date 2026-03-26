package com.prakhar.coretrading.feign;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "market-ai-service")
public interface MarketAiClient {
    @GetMapping("/api/coins/details/{coinId}")
    JsonNode getCoinDetails(@PathVariable String coinId);
}
