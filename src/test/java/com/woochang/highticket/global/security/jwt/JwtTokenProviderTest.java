package com.woochang.highticket.global.security.jwt;

import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.domain.user.security.CustomOAuth2User;
import com.woochang.highticket.global.config.JwtProperties;
import com.woochang.highticket.global.security.oauth2.OAuth2Attribute;
import com.woochang.highticket.service.user.CustomOAuth2UserService;
import com.woochang.highticket.support.JwtTestUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomOAuth2UserService customOAuth2UserService;

    private User user;
    private Authentication auth;


    @BeforeEach
    public void setUp() {
        // 10초, 1분, 2분
        JwtProperties jwtProperties = new JwtProperties(10_000, 60_000, 120_000);
        jwtTokenProvider = new JwtTokenProvider(jwtProperties, customOAuth2UserService);
        jwtTokenProvider.init();

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
    @DisplayName("AccessToken이 정상적으로 생성된다")
    public void createAccessToken_success() {
        // given -> setUp

        // when
        String accessToken = jwtTokenProvider.createAccessToken(auth);

        // then
        assertNotNull(accessToken);
        Claims claims = jwtTokenProvider.parseClaims(accessToken);

        assertEquals(user.getUserIdString(), claims.getSubject());
        assertEquals(user.getEmail(), claims.get("email", String.class));
        assertEquals(user.getNickname(), claims.get("name", String.class));
        assertEquals(user.getRole().toString(), claims.get("role", String.class));
    }

    @Test
    @DisplayName("RefreshToken이 정상적으로 생성된다")
    public void createRefreshToken_success() {
        // given -> setUp

        // when
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        // then
        assertNotNull(refreshToken);
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        assertEquals(user.getUserIdString(), claims.getSubject());
    }

    @Test
    @DisplayName("AccessToken으로 Authentication을 생성할 수 있다")
    public void getAuthentication_success() {
        // given
        String token = jwtTokenProvider.createAccessToken(auth);

        when(customOAuth2UserService.loadByUserId(user.getUserIdString()))
                .thenReturn((CustomOAuth2User) auth.getPrincipal());

        // when
        Authentication result = jwtTokenProvider.getAuthentication(token);

        // then
        assertEquals(user.getUserIdString(), result.getName());
        assertEquals(auth.getPrincipal(), result.getPrincipal());

    }

    @Test
    @DisplayName("잘못된 키로 서명된 토큰은 false를 반환해야 한다")
    public void validateToken_invalidSignature() throws NoSuchAlgorithmException {
        // given 
        KeyPairGenerator key = KeyPairGenerator.getInstance("RSA");
        key.initialize(2048);
        KeyPair keyPair = key.generateKeyPair();

        String fakeToken = Jwts.builder()
                .subject("1")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(keyPair.getPrivate())
                .compact();

        // when
        boolean result = jwtTokenProvider.validateToken(fakeToken);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("위조된 Payload의 토큰은 false를 반환해야 한다")
    public void validateToken_forgedPayload() {
        // given
        String originalJwt = jwtTokenProvider.createAccessToken(auth);

        // 토큰 분해
        String[] parts = originalJwt.split("\\.");
        String header = parts[0];
        String payload = parts[1];

        // payload 디코딩 -> 조작 -> 다시 인코딩
        String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));
        String forgedPayload = decodedPayload.replace(user.getEmail(), "forgedEmail@example.com");
        String reEncodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(forgedPayload.getBytes());


        // 위조된 토큰 조합
        String forgedToken = header + "." + reEncodedPayload + "." + parts[2];

        // when
        boolean result = jwtTokenProvider.validateToken(forgedToken);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("만료된 Access Token은 false를 반환해야 한다")
    public void validateToken_expiredAccessToken() {
        // given
        String token = JwtTestUtils.createExpiredAccessToken(
                (PrivateKey) ReflectionTestUtils.getField(jwtTokenProvider, "privateKey"),
                "1"
        );

        // when
        boolean result = jwtTokenProvider.validateToken(token);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("만료된 Refresh Token은 false를 반환해야 한다")
    public void validateToken_expiredRefreshToken() {
        // given
        String token = JwtTestUtils.createExpiredRefreshToken(
                (PrivateKey) ReflectionTestUtils.getField(jwtTokenProvider, "privateKey"),
                "1"
        );

        // when
        boolean result = jwtTokenProvider.validateToken(token);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("AccessToken에서 사용자 ID를 추출할 수 있다")
    public void getUserId_success() {
        // given 
        String token = jwtTokenProvider.createAccessToken(auth);

        // when
        String result = jwtTokenProvider.getUserId(token);

        // then
        assertEquals(user.getUserIdString(), result);
    }

    @Test
    @DisplayName("AccessToken에서 만료 시각을 추출할 수 있다")
    public void getExpiration_success() {
        // given
        String token = jwtTokenProvider.createAccessToken(auth);

        // when
        Date expiration = jwtTokenProvider.getExpiration(token);

        // then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("AccessToken의 남은 만료 시간을 계산할 수 있다")
    public void getAccessTokenRemainingExpiryMs_success() {
        // given
        String token = jwtTokenProvider.createAccessToken(auth);

        // when
        long remaining = jwtTokenProvider.getAccessTokenRemainingExpiryMs(token);

        // then
        assertTrue(remaining <= 10_000);
        assertTrue(remaining >0);
    }

    @Test
    @DisplayName("유효하지 않은 AccessToken이면 남은 만료 시간이 0이어야 한다")
    public void getAccessTokenRemainingExpiryMs_returnZero() {
        // given
        String token = "invalidToken";

        // when
        long remaining = jwtTokenProvider.getAccessTokenRemainingExpiryMs(token);

        // then
        assertEquals(0, remaining);
    }

}
