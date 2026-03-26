package com.prakhar.marketai.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface MarketAiService {
    JsonNode getCoinList(int page) throws Exception;
    JsonNode getTop50() throws Exception;
    JsonNode searchCoin(String query) throws Exception;
    JsonNode getMarketChart(String coinId, int days) throws Exception;
    JsonNode getCoinDetails(String coinId) throws Exception;
    String getAiResponse(String prompt);
}
