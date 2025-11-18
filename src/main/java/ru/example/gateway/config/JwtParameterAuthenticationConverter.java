package ru.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtParameterAuthenticationConverter
    implements Converter<ServerWebExchange, Mono<Jwt>> {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    private ReactiveJwtDecoder jwtDecoder;

    @Override
    public Mono<Jwt> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(extractToken(exchange.getRequest()))
            .flatMap(token -> getJwtDecoder().decode(token));
    }

    private String extractToken(ServerHttpRequest request) {
        var token = request.getQueryParams().getFirst("access_token");
        if (token == null) {
            var cookie = request.getCookies().getFirst("JWT");
            token = cookie != null ? cookie.getValue() : null;
        }
        if (token == null) {
            var authHeader = request.getHeaders().getFirst("Authorization");
            token = authHeader != null && authHeader.startsWith("Bearer ") ?
                authHeader.substring(7) : null;
        }
        return token;
    }

    private ReactiveJwtDecoder getJwtDecoder() {
        if (jwtDecoder == null) {
            jwtDecoder =
                NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        }
        return jwtDecoder;
    }
}