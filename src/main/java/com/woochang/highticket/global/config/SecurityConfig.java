package com.woochang.highticket.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@Profile("!test")
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Basic Auth 비활성화
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Form 로그인 비활성화
                .logout(ServerHttpSecurity.LogoutSpec::disable) // Logout 비활성화
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 보호 비활성화
                .authorizeExchange(exchanges -> {
                    exchanges
                            .pathMatchers("/", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                            .anyExchange().denyAll();
                })
                .oauth2Login(Customizer.withDefaults()) // OAuth2 로그인 활성화
                .build();
    }
}
