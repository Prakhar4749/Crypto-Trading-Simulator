package com.prakhar.coretrading.filter;

import com.prakhar.common.util.LogUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(MdcFilter.class);

    @Value("${spring.application.name:core-trading-service}")
    private String serviceName;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain)
        throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Set MDC context
        String traceId = request
          .getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString()
              .substring(0, 8);
        }

        String userId = request
          .getHeader("X-User-Id");
        String path = request.getMethod() 
          + " " + request.getRequestURI();

        MDC.put("traceId", traceId);
        MDC.put("userId", 
          userId != null ? userId : "anonymous");
        MDC.put("service", serviceName);

        // Log incoming request
        log.info(LogUtil.info(
          serviceName, path,
          userId,
          "⟶ REQUEST IN | traceId=" + traceId
        ));

        try {
            chain.doFilter(request, response);
        } finally {
            long elapsed = 
              System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            // Log slow requests
            if (elapsed > 1000) {
                log.warn(LogUtil.slow(
                  serviceName, path, userId, elapsed
                ));
            }

            // Log response
            log.info(LogUtil.info(
              serviceName, path, userId,
              "⟵ RESPONSE OUT | status=" + status 
              + (elapsed > 1000 
                ? " | ⚠️ " + elapsed + "ms" : "")
            ));

            MDC.clear();
        }
    }
}
