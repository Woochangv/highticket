package com.woochang.highticket.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woochang.highticket.global.util.HttpResponseWriters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.woochang.highticket.global.exception.ErrorCode.AUTH_ACCESS_DENIED;
import static com.woochang.highticket.global.response.ApiResponse.error;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpResponseWriters.writeJson(response, AUTH_ACCESS_DENIED.getStatus(), error(AUTH_ACCESS_DENIED), objectMapper);
    }
}
