package com.woochang.highticket.global.security.oauth2;

import com.woochang.highticket.dto.auth.TokenDto;
import com.woochang.highticket.service.auth.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        TokenDto tokenDto = tokenService.issueToken(auth);

        // 헤더 토큰 추가
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());

        // TODO: 상태 코드 설정 또는 응답 바디 작성 (고려)
    }
}
