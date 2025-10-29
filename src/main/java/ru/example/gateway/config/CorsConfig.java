package ru.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class CorsConfig {

          @Bean
          public WebFilter corsFilter() {
                    return (ServerWebExchange exchange, WebFilterChain chain) -> {
                              ServerHttpRequest request = exchange.getRequest();
                              ServerHttpResponse response = exchange.getResponse();
                              HttpHeaders headers = response.getHeaders();

                              headers.add("Access-Control-Allow-Origin", "http://localhost:3000");
                              headers.add("Access-Control-Allow-Methods",
                                        "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                              headers.add("Access-Control-Allow-Headers", "*");
                              headers.add("Access-Control-Allow-Credentials", "true");
                              headers.add("Access-Control-Expose-Headers", "Authorization, Content-Type");
                              headers.add("Access-Control-Max-Age", "3600");

                              if (request.getMethod() == HttpMethod.OPTIONS) {
                                        response.setStatusCode(HttpStatus.OK);
                                        return response.setComplete(); // Используйте setComplete() вместо Mono.empty()
                              }

                              return chain.filter(exchange);
                    };
          }
}