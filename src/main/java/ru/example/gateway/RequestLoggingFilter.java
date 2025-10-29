package ru.example.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class RequestLoggingFilter implements WebFilter {

          private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

          @Override
          public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                    ServerHttpRequest request = exchange.getRequest();

                    // Логируем все входящие запросы
                    log.info("=== FRONTEND REQUEST ===");
                    log.info("Method: {}", request.getMethod());
                    log.info("Path: {}", request.getPath());
                    log.info("Headers: {}", request.getHeaders());
                    log.info("Remote Address: {}", request.getRemoteAddress());
                    log.info("User Agent: {}", request.getHeaders().getFirst("User-Agent"));
                    log.info("Origin: {}", request.getHeaders().getFirst("Origin"));
                    log.info("Referer: {}", request.getHeaders().getFirst("Referer"));
                    log.info("========================");

                    return chain.filter(exchange);
          }
}