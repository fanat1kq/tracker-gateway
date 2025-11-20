package ru.example.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();

        String requestId = exchange.getRequest().getId();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getPath().value();
        String queryParams = exchange.getRequest().getQueryParams().toString();
        String headers = exchange.getRequest().getHeaders().toString();

        // Log incoming request
        logger.info("=== GATEWAY REQUEST START ===");
        logger.info("Request ID: {}", requestId);
        logger.info("Method: {}", method);
        logger.info("Path: {}", path);
        logger.info("Query Params: {}", queryParams);
        logger.info("Headers: {}", headers);
        logger.info("Remote Address: {}", exchange.getRequest().getRemoteAddress());

        return chain.filter(exchange)
            .doOnSuccess(aVoid -> {
                logResponse(exchange, startTime, requestId, method, path, null);
            })
            .doOnError(throwable -> {
                logResponse(exchange, startTime, requestId, method, path, throwable);
            });
    }

    private void logResponse(ServerWebExchange exchange, Instant startTime, String requestId,
                             String method, String path, Throwable throwable) {
        Instant endTime = Instant.now();
        long duration = Duration.between(startTime, endTime).toMillis();

        int statusCode = exchange.getResponse().getStatusCode() != null ?
            exchange.getResponse().getStatusCode().value() : 0;

        logger.info("=== GATEWAY REQUEST END ===");
        logger.info("Request ID: {}", requestId);
        logger.info("Method: {} | Path: {}", method, path);
        logger.info("Status: {} | Duration: {}ms", statusCode, duration);

        if (throwable != null) {
            logger.error("Error processing request: {}", throwable.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}