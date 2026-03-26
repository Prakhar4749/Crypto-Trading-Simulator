package com.prakhar.coretrading.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "integration.razorpay")
public class RazorpayConfig {
    private String keyId;
    private String keySecret;
    private long exchangeRate;

    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    public String getKeySecret() { return keySecret; }
    public void setKeySecret(String keySecret) { this.keySecret = keySecret; }
    public long getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(long exchangeRate) { this.exchangeRate = exchangeRate; }
}