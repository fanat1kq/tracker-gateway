package ru.example.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtParameterAuthenticationConverter implements
          Converter<ServerWebExchange, Mono<Jwt>> {

          @Override
          public Mono<Jwt> convert(ServerWebExchange exchange) {
                    ServerHttpRequest request = exchange.getRequest();

                    // ✅ 1. Пробуем извлечь JWT из параметра access_token
                    String token = request.getQueryParams().getFirst("access_token");

                    // ✅ 2. Если нет в параметре, пробуем из cookie
                    if (token == null) {
                              HttpCookie jwtCookie = request.getCookies().getFirst("JWT");
                              if (jwtCookie != null) {
                                        token = jwtCookie.getValue();
                              }
                    }

                    // ✅ 3. Если нет в cookie, пробуем из заголовка Authorization
                    if (token == null) {
                              String authHeader = request.getHeaders().getFirst("Authorization");
                              if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                        token = authHeader.substring(7);
                              }
                    }

                    if (token != null && !token.isEmpty()) {
                              // Декодируем JWT и возвращаем
                              ReactiveJwtDecoder jwtDecoder = createJwtDecoder();
                              return jwtDecoder.decode(token);
                    }

                    return Mono.empty();
          }

          private ReactiveJwtDecoder createJwtDecoder() {
                    return NimbusReactiveJwtDecoder.withJwkSetUri("http://localhost:9000/oauth2/jwks").build();
          }
}
