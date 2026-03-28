package com.prakhar.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class JwtValidationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtValidationFilter.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${INTERNAL_SERVICE_API_KEY}")
    private String internalApiKey;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/signup",
            "/auth/signin",
            "/auth/google",
            "/auth/claim-bonus",
            "/auth/users/reset-password/send-otp",
            "/auth/users/reset-password/verify-otp",
            "/auth/two-factor/otp",
            "/actuator/health",
            "/actuator/info"
    );

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/auth/two-factor/otp/",
            "/auth/users/reset-password/"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Always allow OPTIONS (CORS preflight)
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        // 2. Check exact path whitelist
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::equals);

        // 3. Check prefix whitelist
        if (!isPublic) {
            isPublic = PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
        }

        // Special case for Razorpay Webhook
        if (path.equals("/api/wallet/webhook/razorpay")) {
            isPublic = true;
        }

        // 4. Handle Public Paths
        if (isPublic) {
            log.debug("[Gateway] Forwarding public request to: {} | Path: {}",
                    request.getURI(), path);

            ServerHttpRequest publicRequest = request.mutate()
                    .header("X-Internal-Api-Key", internalApiKey)
                    .header("X-Trace-Id", getOrCreateTraceId(exchange))
                    .build();

            return chain.filter(exchange.mutate().request(publicRequest).build());
        }

        // 5. All other paths -> validate JWT
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Unauthorized: No Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        if (!authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Unauthorized: Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = String.valueOf(claims.get("userId"));
            String email = String.valueOf(claims.get("email"));
            String fullName = String.valueOf(claims.get("fullName"));
            String authorities = String.valueOf(claims.get("authorities"));

            log.debug("[Gateway] Forwarding protected request to: {} | Path: {} | User: {}",
                    request.getURI(), path, email);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-ID", userId)
                    .header("X-User-Email", email)
                    .header("X-User-FullName", fullName)
                    .header("X-User-Role", authorities)
                    .header("X-Internal-Api-Key", internalApiKey)
                    .header("X-Trace-Id", getOrCreateTraceId(exchange))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("[Gateway] JWT validation failed: {}", e.getMessage());
            return onError(exchange, "Unauthorized: Invalid or missing token", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public int getOrder() {
        return -1; // runs first
    }

    private String getOrCreateTraceId(ServerWebExchange exchange) {
        String existing = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        return (existing != null && !existing.isBlank()) ? existing : UUID.randomUUID().toString();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String body = String.format("""
                {
                  "success": false,
                  "message": "%s",
                  "data": null
                }
                """, err);

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
