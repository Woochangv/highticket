package com.woochang.highticket.global.config;

import com.woochang.highticket.global.security.jwt.JwtAccessDeniedHandler;
import com.woochang.highticket.global.security.jwt.JwtAuthenticationEntryPoint;
import com.woochang.highticket.global.security.jwt.JwtAuthenticationFilter;
import com.woochang.highticket.global.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@Profile("dev")
@RequiredArgsConstructor
public class SecurityConfigDev {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Basic Auth 비활성화
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Form 로그인 비활성화
                .logout(ServerHttpSecurity.LogoutSpec::disable) // Logout 비활성화
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 보호 비활성화
                .authorizeExchange(exchanges -> {
                    exchanges
                            .pathMatchers("/",
                                    "/v3/api-docs/**",
                                    "/swagger-ui.html",
                                    "/swagger-ui/**",
                                    "/login/oauth2/**",
                                    "/oauth2/authorization/**",
                                    "/venues/**",
                                    "/performances/**"
                                    ).permitAll()
                            .anyExchange().denyAll();
                })
                .oauth2Login(spec -> spec
                        .authenticationSuccessHandler(oAuth2SuccessHandler)
                ) // OAuth2 로그인 활성화
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(spec -> spec
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .build();
    }
}
