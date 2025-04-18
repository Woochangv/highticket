package com.woochang.highticket.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {

    private final String accessToken;
    private final String refreshToken;
}
