package com.minelog.road.gateway.config;

import com.minelog.road.gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtService jwtService;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    AuthenticationWebFilter jwtAuthFilter = new AuthenticationWebFilter(reactiveAuthenticationManager());
    jwtAuthFilter.setServerAuthenticationConverter(exchange -> {
      String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return Mono.empty();
      }
      String token = authHeader.substring(7);
      Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
      return Mono.just(auth);
    });
    jwtAuthFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange());

    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(exchanges -> exchanges
            .pathMatchers(
                "/api/auth/**",
                "/actuator/health"
            ).permitAll()
            .anyExchange().authenticated()
        )
        .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .build();
  }

  @Bean
  public ReactiveAuthenticationManager reactiveAuthenticationManager() {
    return authentication -> {
      String token = (String) authentication.getCredentials();
      return Mono.fromCallable(() -> {
            try {
              var claims = jwtService.parseToken(token);
              String username = claims.getSubject();
              if (username == null || username.isBlank()) {
                throw new BadCredentialsException("Invalid JWT token");
              }
              // Roles/authorities can be extracted from claims if added later
              return new UsernamePasswordAuthenticationToken(
                  username,
                  token,
                  Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
              );
            } catch (Exception e) {
              throw new BadCredentialsException("Invalid JWT token", e);
            }
          }
      );
    };
  }
}


