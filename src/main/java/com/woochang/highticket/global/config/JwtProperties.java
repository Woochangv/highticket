package com.woochang.highticket.global.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@AllArgsConstructor
public class JwtProperties {

    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;
    private final long refreshTokenRenewThresholdMs;
}
