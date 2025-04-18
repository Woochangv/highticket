package com.woochang.highticket.global.security.jwt;

import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.domain.user.security.CustomUserDetails;
import com.woochang.highticket.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("AccessToken이 정상적으로 생성되는지 확인")
    public void createAccessToken_success() {
        // given
        String email = "test@example.com";
        String loginType = "GOOGLE";
        LocalDateTime createAt = LocalDateTime.now();
        User user = new User(email, loginType, createAt);
        userRepository.save(user);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // when
        String accessToken = jwtTokenProvider.createAccessToken(auth);

        // then
        assertNotNull(accessToken);
        Claims claims = jwtTokenProvider.parseClaims(accessToken);
        assertEquals(user.getId().toString(), claims.getSubject());
        assertEquals(user.getEmail(), claims.get("email", String.class));
    }

    @Test
    @DisplayName("RefreshToken이 정상적으로 생성되는지 확인")
    public void createRefreshToken_success () {
        // given
        String email = "test@example.com";
        String loginType = "GOOGLE";
        LocalDateTime createAt = LocalDateTime.now();
        User user = new User(email, loginType, createAt);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // when
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        // then
        assertNotNull(refreshToken);
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        assertEquals(user.getId().toString(), claims.getSubject());
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
        String email = "test@example.com";
        String loginType = "GOOGLE";
        LocalDateTime createAt = LocalDateTime.now();
        User user = new User(email, loginType, createAt);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

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
    public void validateToken_expiredAccessToken() throws InterruptedException {
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
