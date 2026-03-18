package com.prakhar.gateway.filter;

import com.prakhar.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter 
  implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(
        ServerWebExchange exchange,
        GatewayFilterChain chain) {

        long start = System.currentTimeMillis();
        String path = exchange.getRequest()
          .getMethod() + " " 
          + exchange.getRequest().getPath();
        String userId = exchange.getRequest()
          .getHeaders().getFirst("X-User-Id");

        log.info(LogUtil.info(
          "api-gateway", path, userId,
          "⟶ Routing request"
        ));

        return chain.filter(exchange)
          .doFinally(signal -> {
            long elapsed = 
              System.currentTimeMillis() - start;
            Integer status = exchange.getResponse()
              .getStatusCode() != null
                ? exchange.getResponse()
                    .getStatusCode().value()
                : 0;

            if (elapsed > 1000) {
                log.warn(LogUtil.slow(
                  "api-gateway", path, 
                  userId, elapsed
                ));
            }

            log.info(LogUtil.info(
              "api-gateway", path, userId,
              "⟵ Response | status=" + status
              + (elapsed > 1000 
                ? " | ⚠️ " + elapsed + "ms" : "")
            ));
          });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
