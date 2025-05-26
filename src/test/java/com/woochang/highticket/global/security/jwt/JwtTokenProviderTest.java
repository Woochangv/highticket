package com.woochang.highticket.global.security.jwt;

import com.woochang.highticket.OAuth2TestConfig;
import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.domain.user.security.CustomOAuth2User;
import com.woochang.highticket.global.security.oauth2.OAuth2Attribute;
import com.woochang.highticket.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(OAuth2TestConfig.class)
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    private Authentication auth;
    private User savedUser;

    @BeforeEach
    public void setUp() {
        User user = User.ofOAuth2("test@example.com", "test", LoginType.GOOGLE);

        savedUser = userRepository.save(user);

        Map<String, Object> attributes = Map.of(
                "email", "test@example.com",
                "name", "test"
        );
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of("google", attributes);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(savedUser, oAuth2Attribute.toMap());
        auth = new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), "google");
    }

    @Test
    @DisplayName("AccessToken이 정상적으로 생성되는지 확인")
    public void createAccessToken_success() {
        // given -> init

        // when
        String accessToken = jwtTokenProvider.createAccessToken(auth);

        // then
        assertNotNull(accessToken);
        Claims claims = jwtTokenProvider.parseClaims(accessToken);
        assertEquals(savedUser.getUserIdString(), claims.getSubject());
        assertEquals(savedUser.getEmail(), claims.get("email", String.class));
        assertEquals(savedUser.getNickname(), claims.get("name", String.class));
        assertEquals(savedUser.getRole().toString(), claims.get("role", String.class));
    }

    @Test
    @DisplayName("RefreshToken이 정상적으로 생성되는지 확인")
    public void createRefreshToken_success () {
        // given -> init

        // when
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        // then
        assertNotNull(refreshToken);
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        assertEquals(savedUser.getUserIdString(), claims.getSubject());
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
    public void validateToken_forgedPayload () {
        // given
        String originalJwt = jwtTokenProvider.createAccessToken(auth);

        // 토큰 분해
        String[] parts = originalJwt.split("\\.");
        String header = parts[0];
        String payload = parts[1];

        // payload 디코딩 -> 조작 -> 다시 인코딩
        String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));
        String forgedPayload = decodedPayload.replace(savedUser.getEmail(), "forgedEmail@example.com");
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
        String expiredAccessToken = jwtTokenProvider.createExpiredAccessToken("1");

        // when
        boolean result = jwtTokenProvider.validateToken(expiredAccessToken);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("만료된 Refresh Token은 false를 반환해야 한다")
    public void validateToken_expiredRefreshToken() {
        // given
        String expiredRefreshToken = jwtTokenProvider.createExpiredRefreshToken("1");

        // when
        boolean result = jwtTokenProvider.validateToken(expiredRefreshToken);

        // then
        assertFalse(result);
    }
}
