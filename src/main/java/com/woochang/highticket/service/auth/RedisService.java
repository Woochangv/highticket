package com.woochang.highticket.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // Redis Key 접두어
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String BLACKLIST_PREFIX = "BL:";

    // Refresh Token 저장
    public void saveRefreshToken(String userId, String refreshToken, long ttlMillis) {
        redisTemplate.opsForValue()
                .set(REFRESH_TOKEN_PREFIX + userId, refreshToken, ttlMillis, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 조회
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
    }

    // Refresh Token 제거
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    // Access Token 블랙리스트
    public void blacklistAccessToken(String accessToken, long expirationMs) {
        redisTemplate.opsForValue()
                .set(BLACKLIST_PREFIX + accessToken, "true", expirationMs, TimeUnit.MICROSECONDS);
    }

    // Access Token 사용 가능 여부 검증
    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken);
    }

    public long getTTL(String userId) {
        return redisTemplate.getExpire(REFRESH_TOKEN_PREFIX + userId, TimeUnit.MILLISECONDS);
    }
}
