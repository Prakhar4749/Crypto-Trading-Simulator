package com.prakhar.notification.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InternalApiKeyFilter.class);

    @Value("${INTERNAL_SERVICE_API_KEY}")
    private String expectedKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String receivedKey = request.getHeader("X-Internal-Api-Key");

        if (receivedKey == null || expectedKey == null || !receivedKey.trim().equals(expectedKey.trim())) {
            log.debug("[InternalApiKey] Access Denied: Received key: {} | Expected key hash: {}",
                    receivedKey != null ? "present" : "missing",
                    expectedKey != null ? Integer.toHexString(expectedKey.trim().hashCode()) : "null"
            );
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "success": false,
                  "message": "Forbidden: Invalid or missing Internal API Key",
                  "data": null
                }
                """);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
