package com.prakhar.marketai.service;

public interface MarketAiService {
    Object getCoinList(int page, String currency);
    Object getTop50Coins();
    Object searchCoins(String query);
    Object getCoinChart(String coinId, int days, String currency);
    Object getCoinById(String coinId);
    Object getCoinsByIds(java.util.List<String> ids);
    Object getTrendingCoins();
    String chat(String message, String userId);
}
