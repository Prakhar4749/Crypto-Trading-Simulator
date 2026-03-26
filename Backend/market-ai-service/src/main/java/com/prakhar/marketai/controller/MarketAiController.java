package com.prakhar.marketai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.prakhar.marketai.dto.request.ChatRequest;
import com.prakhar.marketai.service.MarketAiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MarketAiController {

    private final MarketAiService marketAiService;

    public MarketAiController(MarketAiService marketAiService) {
        this.marketAiService = marketAiService;
    }

    @GetMapping("/coins")
    public ResponseEntity<JsonNode> getCoinList(@RequestParam(defaultValue = "1") int page) throws Exception {
        return ResponseEntity.ok(marketAiService.getCoinList(page));
    }

    @GetMapping("/coins/top50")
    public ResponseEntity<JsonNode> getTop50() throws Exception {
        return ResponseEntity.ok(marketAiService.getTop50());
    }

    @GetMapping("/coins/search")
    public ResponseEntity<JsonNode> searchCoin(@RequestParam String q) throws Exception {
        return ResponseEntity.ok(marketAiService.searchCoin(q));
    }

    @GetMapping("/coins/{coinId}/chart")
    public ResponseEntity<JsonNode> getMarketChart(@PathVariable String coinId, @RequestParam int days) throws Exception {
        return ResponseEntity.ok(marketAiService.getMarketChart(coinId, days));
    }

    @GetMapping("/coins/details/{coinId}")
    public ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        return ResponseEntity.ok(marketAiService.getCoinDetails(coinId));
    }

    @PostMapping("/chat/bot")
    public ResponseEntity<String> getAiResponse(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(marketAiService.getAiResponse(request.getPrompt()));
    }
}