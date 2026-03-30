package com.prakhar.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    @Value("${ALLOWED_ORIGINS:http://localhost:5173}")
    private String allowedOriginsRaw;
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // ── Read from env var, support comma-separated list ──
        List<String> allowedOrigins = Arrays.stream(allowedOriginsRaw.split(","))
                .map(String::trim)
                .toList();

        corsConfig.setAllowedOrigins(allowedOrigins);
        
        // Allowed HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Allowed headers
        corsConfig.setAllowedHeaders(List.of("*"));
        
        // Allow cookies and Authorization header
        corsConfig.setAllowCredentials(true);
        
        // Cache preflight for 1 hour
        corsConfig.setMaxAge(3600L);
        
        // Expose headers that frontend might need to read
        corsConfig.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Trace-Id"
        ));

        UrlBasedCorsConfigurationSource source = 
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
