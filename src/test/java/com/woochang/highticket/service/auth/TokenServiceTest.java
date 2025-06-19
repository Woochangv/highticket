package com.woochang.highticket.service.auth;

import com.woochang.highticket.OAuth2TestConfig;
import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.domain.user.security.CustomOAuth2User;
import com.woochang.highticket.dto.auth.TokenDto;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.global.exception.InvalidTokenException;
import com.woochang.highticket.global.security.jwt.JwtTokenProvider;
import com.woochang.highticket.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(OAuth2TestConfig.class)
class TokenServiceTest {

    @Autowired
    TokenService tokenService;
    @Autowired
    RedisService redisService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    User savedUser;
    Authentication auth;


    @BeforeEach
    public void setUp() {
        User user = User.ofOAuth2("test@example.com", "test", LoginType.GOOGLE);
        savedUser = userRepository.save(user);
        CustomOAuth2User oAuth2User = new CustomOAuth2User(savedUser, Map.of());
        auth = new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), "google");
    }

    @Test
    @DisplayName("Access/Refresh 토큰 정상 발급")
    public void issueToken() {
        // given -> setUp

        // when
        TokenDto dto = tokenService.issueToken(auth);

        // then
        assertNotNull(dto.getAccessToken());
        assertNotNull(dto.getRefreshToken());
        String savedRedisRefreshToken = redisService.getRefreshToken(savedUser.getUserIdString());
        assertEquals(dto.getRefreshToken(), savedRedisRefreshToken);
    }

    @Test
    @DisplayName("정상적인 Refresh Token으로 Access Token 재발급")
    public void reissueToken_success() {
        // given
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);
        redisService.saveRefreshToken(savedUser.getUserIdString(), refreshToken, jwtTokenProvider.getRefreshTokenExpiryMs());

        // when
        TokenDto dto = tokenService.reissueToken(refreshToken);

        // then
        String accessToken = dto.getAccessToken();
        assertNotNull(accessToken);
        assertEquals(savedUser.getEmail(), jwtTokenProvider.parseClaims(accessToken).get("email", String.class));
    }

    @Test
    @DisplayName("Refresh Token 만료 임박 시 재발급")
    public void reissueToken_refreshToken() throws InterruptedException {
        // given
        String oldRefreshToken = jwtTokenProvider.createRefreshToken(auth);
        redisService.saveRefreshToken(savedUser.getUserIdString(), oldRefreshToken, jwtTokenProvider.getRefreshTokenExpiryMs());
        Thread.sleep(1000); // 오래된 리프레쉬 토큰이므로 1초후 로직들이 진행되게 함 (처리 속도가 빠르므로 같은 토큰으로 인식하는 문제 해결을 위함)

        // when
        TokenDto dto = tokenService.reissueToken(oldRefreshToken);

        // then
        assertNotNull(dto.getAccessToken());
        assertNotNull(dto.getRefreshToken());
        assertNotEquals(oldRefreshToken, dto.getRefreshToken());
    }

    @Test
    @DisplayName("Refresh Token이 유효하지 않은 경우 예외 발생")
    public void reissueToken_invalidRefreshToken() {
        // given
        String fakeToken = "invalidRefreshToken";

        // when & then
        assertInvalidTokenException(() -> tokenService.reissueToken(fakeToken), ErrorCode.AUTH_INVALID_TOKEN);
    }


    @Test
    @DisplayName("Refresh Token은 유효하지만 저장된 토큰이 없는 경우 예외 발생")
    public void reissue_nonExistentRedis() {
        // given
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        // when & then
        assertInvalidTokenException(() -> tokenService.reissueToken(refreshToken), ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("로그아웃 시 저장된 Refresh Token 삭제 및 재사용 불가")
    public void logout_tokenCannotBeUsed() {
        // given
        TokenDto dto = tokenService.issueToken(auth);
        String storedRefreshToken = redisService.getRefreshToken(savedUser.getUserIdString());
        assertThat(storedRefreshToken).isEqualTo(dto.getRefreshToken());

        // when
        tokenService.logout(savedUser.getUserIdString(), dto.getAccessToken());

        // then
        assertNull(redisService.getRefreshToken(savedUser.getUserIdString()));
        assertInvalidTokenException(() -> tokenService.reissueToken(dto.getRefreshToken()), ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
    }

    // InvalidTokenException 헬퍼 메서드
    private void assertInvalidTokenException(Runnable executable, ErrorCode expectedErrorCode) {
        InvalidTokenException ex = assertThrows(InvalidTokenException.class, executable::run);
        assertThat(ex.getErrorCode()).isEqualTo(expectedErrorCode);
        assertThat(ex.getMessage()).isEqualTo(expectedErrorCode.getMessage());

    }
}