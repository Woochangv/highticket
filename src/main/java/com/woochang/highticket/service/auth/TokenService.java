package com.woochang.highticket.service.auth;

import com.woochang.highticket.dto.auth.TokenDto;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.global.exception.InvalidTokenException;
import com.woochang.highticket.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final UserDetailsService userDetailsService;

    public TokenDto issueToken(Authentication auth) {
        String userId = auth.getName();
        String accessToken = jwtTokenProvider.createAccessToken(auth);
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);
        redisService.saveRefreshToken(userId, refreshToken, jwtTokenProvider.getRefreshTokenExpiryMs());

        return new TokenDto(accessToken, refreshToken);
    }

    public TokenDto reissueToken(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);
        String storedToken = redisService.getRefreshToken(userId);

        if (!refreshToken.equals(storedToken)) {
            throw new InvalidTokenException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = jwtTokenProvider.createAccessToken(auth);

        long ttl = redisService.getTTL(userId);

        if (ttl < jwtTokenProvider.getRefreshTokenRenewThresholdMs()) {
            String newRefreshToken = jwtTokenProvider.createRefreshToken(auth);
            redisService.saveRefreshToken(userId, newRefreshToken, jwtTokenProvider.getRefreshTokenExpiryMs());
            return new TokenDto(newAccessToken, newRefreshToken);
        }

        return new TokenDto(newAccessToken, refreshToken);
    }

    public void logout(String userId, String accessToken) {
        redisService.deleteRefreshToken(userId);

        long remainingExpiryMs = jwtTokenProvider.getAccessTokenRemainingExpiryMs(accessToken);
        if (remainingExpiryMs > 0 && !redisService.isBlacklisted(accessToken)) {
            redisService.blacklistAccessToken(accessToken, remainingExpiryMs);
        }
    }


}
