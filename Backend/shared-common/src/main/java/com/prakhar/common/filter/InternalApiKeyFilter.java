package com.prakhar.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InternalApiKeyFilter.class);

    @Value("${internal.service.api.key:${INTERNAL_SERVICE_API_KEY:}}")
    private String internalApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestApiKey = request.getHeader("X-Internal-Api-Key");

        if (internalApiKey == null || internalApiKey.isEmpty() || !internalApiKey.equals(requestApiKey)) {
            log.debug("[InternalApiKey] Received key: {} | Expected key hash: {}",
                    requestApiKey != null ? "present" : "missing",
                    internalApiKey != null ? Integer.toHexString(internalApiKey.hashCode()) : "null"
            );
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Forbidden: Invalid or missing Internal API Key\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
