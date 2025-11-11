package ru.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

          @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
          private String jwkSetUri;

          @Bean
          public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                    return http
                              .csrf(ServerHttpSecurity.CsrfSpec::disable)
                              .authorizeExchange(exchanges -> exchanges
                                        .pathMatchers("/api/auth/**").permitAll()
                                        .pathMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                                        .pathMatchers("/api/public/**").permitAll()
                                        .pathMatchers("/api/**").permitAll()
                                        .anyExchange().authenticated()
                              )
                              .oauth2ResourceServer(oauth2 -> oauth2
                                        .jwt(Customizer.withDefaults()))
                              .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                              .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                              .build();
          }

          @Bean
          public ReactiveJwtDecoder jwtDecoder() {
                    return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
          }


}