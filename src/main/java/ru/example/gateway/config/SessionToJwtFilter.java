package ru.example.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SessionToJwtFilter implements GlobalFilter {

          @Override
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                    ServerHttpRequest request = exchange.getRequest();
                    String path = request.getPath().value();

                    // Для маршрутов к Task Service
                    if (path.startsWith("/tasks")) {
                              return exchange.getPrincipal()
                                        .cast(Authentication.class)
                                        .map(authentication -> {
                                                  // ✅ Извлекаем JWT из OAuth2 аутентификации
                                                  String jwtToken = extractJwtFromAuthentication(authentication);

                                                  if (jwtToken != null) {
                                                            // Добавляем JWT в заголовок для Task Service
                                                            ServerHttpRequest mutatedRequest = request.mutate()
                                                                      .header("Authorization", "Bearer " + jwtToken)
                                                                      .build();

                                                            return exchange.mutate().request(mutatedRequest).build();
                                                  }
                                                  return exchange;
                                        })
                                        .defaultIfEmpty(exchange)
                                        .flatMap(chain::filter);
                    }

                    return chain.filter(exchange);
          }

          private String extractJwtFromAuthentication(Authentication authentication) {
                    if (authentication instanceof OAuth2AuthenticationToken) {
                              OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                              // JWT хранится в credentials
                              return oauthToken.getCredentials().toString();
                    }
                    return null;
          }
}