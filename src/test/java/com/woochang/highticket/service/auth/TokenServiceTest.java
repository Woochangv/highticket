package com.woochang.highticket.service.auth;

import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.domain.user.security.CustomUserDetails;
import com.woochang.highticket.dto.auth.TokenDto;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.global.exception.InvalidTokenException;
import com.woochang.highticket.global.security.jwt.JwtTokenProvider;
import com.woochang.highticket.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class TokenServiceTest {

    @Autowired
    TokenService tokenService;
    @Autowired
    RedisService redisService;
    @Autowired
    UserRepository userRepository;
    private User user;
    private User savedUser;
    private CustomUserDetails userDetails;
    private Authentication auth;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void init() {
        String email = "test@example.com";
        String loginType = "GOOGLE";
        LocalDateTime createAt = LocalDateTime.now();
        user = new User(email, loginType, createAt);
        savedUser = userRepository.save(user);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    @DisplayName("토큰 발급")
    public void issueToken() {
        // given -> init

        // when
        TokenDto dto = tokenService.issueToken(auth);

        // then
        assertNotNull(dto.getAccessToken());
        assertNotNull(dto.getRefreshToken());
        String savedRedisRefreshToken = redisService.getRefreshToken(savedUser.getId().toString());
        assertEquals(dto.getRefreshToken(), savedRedisRefreshToken);
    }

    @Test
    @DisplayName("정상적인 Refresh Token으로 Access Token 재발급")
    public void reissueToken_success() {
        // given
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);
        redisService.saveRefreshToken(savedUser.getId().toString(), refreshToken, jwtTokenProvider.getRefreshTokenExpiryMs());

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
        redisService.saveRefreshToken(savedUser.getId().toString(), oldRefreshToken, jwtTokenProvider.getRefreshTokenExpiryMs());
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
        assertInvalidTokenException(() -> tokenService.reissueToken(fakeToken), ErrorCode.INVALID_TOKEN);
    }


    @Test
    @DisplayName("Refresh Token은 유효하지만 저장된 토큰이 없는 경우 예외 발생")
    public void reissue_nonExistentRedis() {
        // given
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        // when & then
        assertInvalidTokenException(() -> tokenService.reissueToken(refreshToken), ErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("로그아웃 시 저장된 Refresh Token 삭제 및 재사용 불가")
    public void logout_tokenCannotBeUsed() {
        // given
        TokenDto dto = tokenService.issueToken(auth);
        String storedRefreshToken = redisService.getRefreshToken(savedUser.getId().toString());
        assertThat(storedRefreshToken).isEqualTo(dto.getRefreshToken());

        // when
        tokenService.logout(savedUser.getId().toString(), dto.getAccessToken());

        // then
        assertNull(redisService.getRefreshToken(savedUser.getId().toString()));
        assertInvalidTokenException(() -> tokenService.reissueToken(dto.getRefreshToken()), ErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    // InvalidTokenException 헬퍼 메서드
    private void assertInvalidTokenException(Runnable executable, ErrorCode expectedErrorCode) {
        InvalidTokenException ex = assertThrows(InvalidTokenException.class, executable::run);
        assertThat(ex.getErrorCode()).isEqualTo(expectedErrorCode);
        assertThat(ex.getMessage()).isEqualTo(expectedErrorCode.getMessage());

    }
}