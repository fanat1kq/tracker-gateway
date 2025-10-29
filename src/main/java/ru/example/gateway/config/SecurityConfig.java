package ru.example.gateway.config;

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

          @Bean
          public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                    return http
                              .csrf(ServerHttpSecurity.CsrfSpec::disable)
                              .authorizeExchange(exchanges -> exchanges
                                        .pathMatchers("/api/auth/**").permitAll()
                                        .pathMatchers("/api/public/**").permitAll()
                                        .pathMatchers("/api/**").permitAll()
                                        .anyExchange().authenticated()
                              )
                              .oauth2ResourceServer(oauth2 -> oauth2
                                                  .jwt(Customizer.withDefaults())
                                        // Spring Security сам обрабатывает JWT
                              )
                              .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                              .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                              // ✅ Встроенный OAuth2 Login - Spring сам обработает callback
//                              .oauth2Login(Customizer.withDefaults())
//                              // ✅ Проверка JWT для ресурсных запросов
//                              .oauth2ResourceServer(oauth2 -> oauth2
//                                        .jwt(Customizer.withDefaults())
//                              )
                              .build();
          }

          @Bean
          public ReactiveJwtDecoder jwtDecoder() {
                    return NimbusReactiveJwtDecoder.withJwkSetUri(
                              "http://localhost:9000/oauth2/jwks").build();
          }


}