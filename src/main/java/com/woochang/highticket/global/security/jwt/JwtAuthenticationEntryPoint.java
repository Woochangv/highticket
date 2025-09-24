package com.woochang.highticket.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woochang.highticket.global.util.HttpResponseWriters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.woochang.highticket.global.exception.ErrorCode.AUTH_UNAUTHORIZED;
import static com.woochang.highticket.global.response.ApiResponse.error;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpResponseWriters.writeJson(response, AUTH_UNAUTHORIZED.getStatus(), error(AUTH_UNAUTHORIZED), objectMapper);
    }
}
