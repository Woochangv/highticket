package com.woochang.highticket.global.config;

import com.woochang.highticket.global.security.jwt.JwtAccessDeniedHandler;
import com.woochang.highticket.global.security.jwt.JwtAuthenticationEntryPoint;
import com.woochang.highticket.global.security.jwt.JwtAuthenticationFilter;
import com.woochang.highticket.global.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("!dev")
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable) // Basic Auth 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form 로그인 비활성화
                .logout(AbstractHttpConfigurer::disable) // Logout 비활성화
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(
                                    "/",
                                    "/v3/api-docs/**",
                                    "/swagger-ui.html", "/swagger-ui/**",
                                    "/login/oauth2/**",
                                    "/oauth2/authorization/**"
                            )
                            .permitAll()
                            .anyRequest()
                            .denyAll();
                })
                .oauth2Login(config -> config
                        .successHandler(oAuth2SuccessHandler)
                ) // OAuth2 로그인 활성화
                .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(config -> config
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .build();
    }
}
