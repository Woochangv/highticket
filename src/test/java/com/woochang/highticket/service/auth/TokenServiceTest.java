package com.woochang.highticket.service.auth;

import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.domain.user.security.CustomOAuth2User;
import com.woochang.highticket.dto.auth.TokenDto;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.global.exception.InvalidTokenException;
import com.woochang.highticket.global.security.jwt.JwtTokenProvider;
import com.woochang.highticket.global.security.oauth2.OAuth2Attribute;
import com.woochang.highticket.service.user.CustomOAuth2UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisService redisService;

    @Mock
    private CustomOAuth2UserService customOAuth2UserService;

    private User user;
    private Authentication auth;


    @BeforeEach
    public void setUp() {
        user = User.ofOAuth2("test@example.com", "test", LoginType.GOOGLE);

        ReflectionTestUtils.setField(user, "id", 1L);

        Map<String, Object> attributes = Map.of(
                "email", "test@example.com",
                "name", "test",
                "loginType", "google"
        );
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of("google", attributes);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(user, oAuth2Attribute.toMap());
        auth = new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), customOAuth2User.getAttribute("loginType"));
    }

    @Test
    @DisplayName("Access/Refresh 토큰이 정상적으로 발급된다")
    public void issueToken_success() {
        // given
        String accessToken = "newAccessToken";
        String refreshToken = "newRefreshToken";

        when(jwtTokenProvider.createAccessToken(auth)).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(auth)).thenReturn(refreshToken);
        when(jwtTokenProvider.getRefreshTokenExpiryMs()).thenReturn(60_000L);

        // when
        TokenDto dto = tokenService.issueToken(auth);

        // then
        verify(redisService).saveRefreshToken(user.getUserIdString(), refreshToken, 60_000L);
        assertNotNull(dto.getAccessToken());
        assertNotNull(dto.getRefreshToken());
        assertEquals(accessToken, dto.getAccessToken());
        assertEquals(refreshToken, dto.getRefreshToken());
    }

    @Test
    @DisplayName("유효한 Refresh Token으로 Access Token만 재발급")
    public void reissueToken_accessToken_success() {
        // given
        String refreshToken = "refreshToken";
        String newAccessToken = "newAccessToken";
        long ttl = 150_000L;
        long renewThresholdMs = 120_000L;


        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(refreshToken)).thenReturn(user.getUserIdString());
        when(redisService.getRefreshToken(user.getUserIdString())).thenReturn(refreshToken);
        when(customOAuth2UserService.loadByUserId(user.getUserIdString())).thenReturn((CustomOAuth2User) auth.getPrincipal());
        when(jwtTokenProvider.createAccessToken(auth)).thenReturn(newAccessToken);
        when(redisService.getTTL(user.getUserIdString())).thenReturn(ttl);
        when(jwtTokenProvider.getRefreshTokenRenewThresholdMs()).thenReturn(renewThresholdMs);


        // when
        TokenDto dto = tokenService.reissueToken(refreshToken);

        // then
        verify(jwtTokenProvider).createAccessToken(auth);
        assertNotNull(dto.getAccessToken());
        assertEquals(refreshToken, dto.getRefreshToken());
    }

    @Test
    @DisplayName("Refresh Token 만료 임박 시 Refresh Token도 재발급")
    public void reissueToken_refreshToken_success() {
        // given
        String oldRefreshToken = "oldRefreshToken";
        String newRefreshToken = "newRefreshToken";
        String newAccessToken = "newAccessToken";
        long ttl = 119_000L;
        long renewThresholdMs = 120_000L;
        long refreshTokenExpiryMs = 60_000L;


        when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(oldRefreshToken)).thenReturn(user.getUserIdString());
        when(redisService.getRefreshToken(user.getUserIdString())).thenReturn(oldRefreshToken);
        when(customOAuth2UserService.loadByUserId(user.getUserIdString())).thenReturn((CustomOAuth2User) auth.getPrincipal());
        when(jwtTokenProvider.createAccessToken(auth)).thenReturn(newAccessToken);
        when(redisService.getTTL(user.getUserIdString())).thenReturn(ttl);
        when(jwtTokenProvider.getRefreshTokenRenewThresholdMs()).thenReturn(renewThresholdMs);
        when(jwtTokenProvider.createRefreshToken(auth)).thenReturn(newRefreshToken);
        when(jwtTokenProvider.getRefreshTokenExpiryMs()).thenReturn(refreshTokenExpiryMs);
        doNothing().when(redisService).saveRefreshToken(user.getUserIdString(), newRefreshToken, refreshTokenExpiryMs);


        // when
        TokenDto dto = tokenService.reissueToken(oldRefreshToken);

        // then
        verify(jwtTokenProvider).createAccessToken(auth);
        verify(jwtTokenProvider).createRefreshToken(auth);
        verify(redisService).saveRefreshToken(user.getUserIdString(), newRefreshToken, refreshTokenExpiryMs);
        assertNotNull(dto.getAccessToken());
        assertNotNull(dto.getRefreshToken());
        assertNotEquals(oldRefreshToken, dto.getRefreshToken());
    }

    @Test
    @DisplayName("Refresh Token이 유효하지 않은 경우 예외가 발생한다")
    public void reissueToken_invalidRefreshToken() {
        // given
        String fakeToken = "invalidRefreshToken";

        when(jwtTokenProvider.validateToken(fakeToken)).thenReturn(false);

        // when & then
        assertInvalidTokenException(() -> tokenService.reissueToken(fakeToken), ErrorCode.AUTH_INVALID_TOKEN);
    }


    @Test
    @DisplayName("Refresh Token은 유효하지만 저장된 토큰이 없는 경우 예외가 발생한다")
    public void reissue_nonExistentRedis() {
        // given
        String refreshToken = "refreshToken";

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(refreshToken)).thenReturn(user.getUserIdString());
        when(redisService.getRefreshToken(user.getUserIdString())).thenReturn(null);

        // when & then
        assertInvalidTokenException(() -> tokenService.reissueToken(refreshToken), ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("Redis에 저장된 Refresh Token과 입력된 토큰이 다르면 예외가 발생한다")
    public void reissue_tokenMismatch() {
        // given
        String inputToken = "inputToken";
        String storedToken = "differentToken";

        when(jwtTokenProvider.validateToken(inputToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(inputToken)).thenReturn(user.getUserIdString());
        when(redisService.getRefreshToken(user.getUserIdString())).thenReturn(storedToken);

        // when & then
        assertInvalidTokenException(() -> tokenService.reissueToken(inputToken), ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("만료된 Access Token은 블랙리스트에 등록되지 않는다")
    void logout_withExpiredAccessToken_doesNotBlacklist() {
        // given
        String accessToken = "expiredToken";
        long remainingExpiryMs = 0L;

        when(jwtTokenProvider.getAccessTokenRemainingExpiryMs(accessToken)).thenReturn(remainingExpiryMs);

        // when
        tokenService.logout(user.getUserIdString(), accessToken);

        // then
        verify(redisService).deleteRefreshToken(user.getUserIdString());
        verify(redisService, never()).blacklistAccessToken(accessToken, remainingExpiryMs);
    }

    @Test
    @DisplayName("이미 블랙리스트에 등록된 토큰은 다시 등록하지 않는다")
    void logout_alreadyBlacklisted_doesNotReBlacklist() {
        // given
        String accessToken = "blacklistedToken";
        long remainingExpiryMs = 1000L;

        when(jwtTokenProvider.getAccessTokenRemainingExpiryMs(accessToken)).thenReturn(remainingExpiryMs);
        when(redisService.isBlacklisted(accessToken)).thenReturn(true);

        // when
        tokenService.logout(user.getUserIdString(), accessToken);

        // then
        verify(redisService).deleteRefreshToken(user.getUserIdString());
        verify(redisService, never()).blacklistAccessToken(accessToken, remainingExpiryMs);
    }

    @Test
    @DisplayName("유효한 Access Token은 블랙리스트에 등록된다")
    void logout_validToken_addsToBlacklist() {
        // given
        String accessToken = "validToken";
        long remainingExpiryMs = 3000L;

        when(jwtTokenProvider.getAccessTokenRemainingExpiryMs(accessToken)).thenReturn(remainingExpiryMs);
        when(redisService.isBlacklisted(accessToken)).thenReturn(false);

        // when
        tokenService.logout(user.getUserIdString(), accessToken);

        // then
        verify(redisService).deleteRefreshToken(user.getUserIdString());
        verify(redisService).blacklistAccessToken(accessToken, remainingExpiryMs);
    }

    // InvalidTokenException 헬퍼 메서드
    private void assertInvalidTokenException(Runnable executable, ErrorCode expectedErrorCode) {
        InvalidTokenException ex = assertThrows(InvalidTokenException.class, executable::run);
        assertThat(ex.getErrorCode()).isEqualTo(expectedErrorCode);
        assertThat(ex.getMessage()).isEqualTo(expectedErrorCode.getMessage());
    }
}