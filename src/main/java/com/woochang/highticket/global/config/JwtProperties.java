package com.woochang.highticket.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@RequiredArgsConstructor
public class JwtProperties {

    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;
    private final long refreshTokenRenewThresholdMs;
}
