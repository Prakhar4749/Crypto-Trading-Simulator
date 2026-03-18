package com.prakhar.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Component
public class MdcWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(
        ServerWebExchange exchange,
        WebFilterChain chain) {

        String traceId = exchange.getRequest()
            .getHeaders()
            .getFirst("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID()
              .toString().substring(0, 8);
        }

        String userId = exchange.getRequest()
            .getHeaders()
            .getFirst("X-User-Id");

        MDC.put("traceId", traceId);
        MDC.put("userId", 
          userId != null ? userId : "anonymous");

        return chain.filter(exchange)
            .doFinally(signal -> MDC.clear());
    }
}
