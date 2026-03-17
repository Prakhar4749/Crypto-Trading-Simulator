package com.prakhar.gateway.config;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class MdcFilter implements WebFilter {

    @Value("${spring.application.name}")
    private String serviceName;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

        MDC.put("traceId", traceId);
        MDC.put("userId", userId != null ? userId : "anonymous");
        MDC.put("service", serviceName);

        // Mutate request to include traceId if it was generated
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Trace-Id", traceId)
                        .build())
                .build();

        return chain.filter(mutatedExchange).doFinally(signalType -> MDC.clear());
    }
}
