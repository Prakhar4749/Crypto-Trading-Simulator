package com.prakhar.marketai.controller;

import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.exception.BusinessException;
import com.prakhar.common.util.LogUtil;
import com.prakhar.marketai.dto.request.ChatRequest;
import com.prakhar.marketai.service.MarketAiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MarketAiController {

    private static final Logger log = LoggerFactory.getLogger(MarketAiController.class);
    private final MarketAiService marketAiService;

    public MarketAiController(MarketAiService marketAiService) {
        this.marketAiService = marketAiService;
    }

    // ═══ COIN ENDPOINTS ═══

    @GetMapping("/coins")
    public ResponseEntity<ApiResponse<Object>> getCoinList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "usd") String currency,
            HttpServletRequest request) {
        
        String userId = request.getHeader("X-User-Id");
        log.info(LogUtil.info(
            "market-ai-service",
            "GET /api/coins", userId,
            "Fetching coin list page=" + page
        ));
        
        Object coins = marketAiService.getCoinList(page, currency);
        return ResponseEntity.ok(ApiResponse.success("Coins fetched", coins));
    }

    @GetMapping("/coins/{coinId}")
    public ResponseEntity<ApiResponse<Object>> getCoinById(
            @PathVariable String coinId,
            HttpServletRequest request) {

        String userId = request.getHeader("X-User-Id");
        log.info(LogUtil.info(
            "market-ai-service",
            "GET /api/coins/" + coinId, 
            userId, "Fetching coin details"
        ));

        Object coin = marketAiService.getCoinById(coinId);
        return ResponseEntity.ok(ApiResponse.success("Coin fetched", coin));
    }

    @GetMapping("/coins/bulk")
    public ResponseEntity<ApiResponse<Object>> getCoinsByIds(
            @RequestParam String ids,
            HttpServletRequest request) {

        String userId = request.getHeader("X-User-Id");
        java.util.List<String> idList = java.util.Arrays.asList(ids.split(","));
        
        log.info(LogUtil.info(
            "market-ai-service",
            "GET /api/coins/bulk", userId,
            "Fetching bulk coins count=" + idList.size()
        ));

        Object coins = marketAiService.getCoinsByIds(idList);
        return ResponseEntity.ok(ApiResponse.success("Bulk coins fetched", coins));
    }

    @GetMapping("/coins/{coinId}/chart")
    public ResponseEntity<ApiResponse<Object>> getCoinChart(
            @PathVariable String coinId,
            @RequestParam(defaultValue = "1") int days,
            @RequestParam(defaultValue = "usd") String currency,
            HttpServletRequest request) {

        String userId = request.getHeader("X-User-Id");
        log.info(LogUtil.info(
            "market-ai-service",
            "GET /api/coins/" + coinId + "/chart",
            userId, "Fetching chart days=" + days
        ));

        Object chart = marketAiService.getCoinChart(coinId, days, currency);
        return ResponseEntity.ok(ApiResponse.success("Chart fetched", chart));
    }

    // ═══ MARKET ENDPOINTS ═══

    @GetMapping("/market/trending")
    public ResponseEntity<ApiResponse<Object>> getTrendingCoins(
            HttpServletRequest request) {

        Object trending = marketAiService.getTrendingCoins();
        return ResponseEntity.ok(ApiResponse.success("Trending coins fetched", trending));
    }

    @GetMapping("/market/search")
    public ResponseEntity<ApiResponse<Object>> searchCoins(
            @RequestParam String query,
            HttpServletRequest request) {

        if (query == null || query.isBlank()) {
            throw new BusinessException("Search query cannot be empty");
        }

        Object results = marketAiService.searchCoins(query);
        return ResponseEntity.ok(ApiResponse.success("Search results", results));
    }

    @GetMapping("/market/top50")
    public ResponseEntity<ApiResponse<Object>> getTop50(HttpServletRequest request) {

        Object top50 = marketAiService.getTop50Coins();
        return ResponseEntity.ok(ApiResponse.success("Top 50 coins", top50));
    }

    // ═══ CHAT ENDPOINT ═══

    @PostMapping("/chat/bot")
    public ResponseEntity<ApiResponse<String>> chat(
            @Valid @RequestBody ChatRequest request,
            HttpServletRequest httpRequest) {

        String userId = httpRequest.getHeader("X-User-Id");

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new BusinessException("Message cannot be empty");
        }

        String reply = marketAiService.chat(request.getMessage(), userId);
        return ResponseEntity.ok(ApiResponse.success("AI response", reply));
    }
}
