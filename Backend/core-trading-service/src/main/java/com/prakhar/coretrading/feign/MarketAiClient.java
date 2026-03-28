package com.prakhar.coretrading.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.prakhar.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "market-ai-service")
public interface MarketAiClient {
    @GetMapping("/api/coins/{coinId}")
    ApiResponse<JsonNode> getCoinDetails(@PathVariable("coinId") String coinId);

    @GetMapping("/api/coins/bulk")
    ApiResponse<JsonNode> getBulkCoins(@RequestParam("ids") String ids);
}
