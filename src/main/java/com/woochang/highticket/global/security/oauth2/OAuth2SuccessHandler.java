package com.woochang.highticket.global.security.oauth2;

import com.woochang.highticket.dto.auth.TokenDto;
import com.woochang.highticket.service.auth.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements ServerAuthenticationSuccessHandler {

    private final TokenService tokenService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication auth) {
        ServerHttpResponse response = exchange.getExchange().getResponse();
        TokenDto tokenDto = tokenService.issueToken(auth);

        response.getHeaders().add("Authorization", "Bearer " + tokenDto.getAccessToken());

        return response.setComplete();
    }
}
