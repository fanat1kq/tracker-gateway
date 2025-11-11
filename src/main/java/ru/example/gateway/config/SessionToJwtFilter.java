package ru.example.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SessionToJwtFilter implements GlobalFilter {

          @Override
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                    var request = exchange.getRequest();

                    if (!request.getPath().value().startsWith("/tasks")) {
                              return chain.filter(exchange);
                    }

                    return exchange.getPrincipal()
                              .cast(Authentication.class)
                              .map(this::extractJwtToken)
                              .map(jwt -> jwt != null ? addAuthHeader(exchange, jwt) : exchange)
                              .defaultIfEmpty(exchange)
                              .flatMap(chain::filter);
          }

          private String extractJwtToken(Authentication authentication) {
                    return authentication instanceof OAuth2AuthenticationToken token
                              ? token.getCredentials().toString()
                              : null;
          }

          private ServerWebExchange addAuthHeader(ServerWebExchange exchange, String jwtToken) {
                    var newRequest = exchange.getRequest().mutate()
                              .header("Authorization", "Bearer " + jwtToken)
                              .build();
                    return exchange.mutate().request(newRequest).build();
          }
}