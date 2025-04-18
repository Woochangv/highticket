package com.woochang.highticket.global.security.jwt;

import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.domain.user.security.CustomUserDetails;
import com.woochang.highticket.global.config.JwtProperties;
import com.woochang.highticket.global.util.PemUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;
    private final long refreshTokenRenewThresholdMs;
    private final UserDetailsService userDetailsService;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtTokenProvider(JwtProperties jwtProperties, UserDetailsService userDetailsService) {
        this.accessTokenExpiryMs = jwtProperties.getAccessTokenExpiryMs();
        this.refreshTokenExpiryMs = jwtProperties.getRefreshTokenExpiryMs();
        this.refreshTokenRenewThresholdMs = jwtProperties.getRefreshTokenRenewThresholdMs();
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        this.privateKey = PemUtils.readPrivateKey("src/main/resources/jwt/private.pem");
        this.publicKey = PemUtils.readPublicKey("src/main/resources/jwt/public.pem");
    }

    // 사용자 인증 정보 기반으로 Access Token 생성
    public String createAccessToken(Authentication auth) {
        String userId = auth.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiryMs);
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        return Jwts.builder()
                .subject(userId)
                .claim("email",user.getEmail())
//                .claim("name", user.getName()) // TODO: OAuth 사용자 정보에서 이름 또는 별명을 받을 수 있음 -> 추후 필드 확장 고려
//                .claim("role", user.getRole()) // TODO: Role도 사용자 도메인에 정의 X -> 추후 확장 고려
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(Authentication auth) {
        String userId = auth.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiryMs);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }

    // 토큰에서 Authentication 객체 생성
    public Authentication getAuthentication(String jwt) {
        String userId = parseClaims(jwt).getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    }


    // 유효한 토큰인지 서명 및 만료 확인
    public boolean validateToken(String jwt) {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (Exception e) {
            log.warn("[JWT 위조 감지] 서명 검증 실패 또는 형식 오류 -> {}", jwt.length() > 20 ? jwt.substring(0, 20) : jwt);
            return false;
        }
    }
    // 토큰에서 사용자 ID 추출
    public String getUserId(String jwt) {
        return parseClaims(jwt).getSubject();
    }

    // 토큰 만료 시각 추출
    public Date getExpiration(String jwt) {
        return parseClaims(jwt).getExpiration();
    }

    public long getRefreshTokenExpiryMs() {
        return refreshTokenExpiryMs;
    }

    public long getRefreshTokenRenewThresholdMs() {
        return refreshTokenRenewThresholdMs;
    }

    // Access Token 남은 만료 시간
    public long getAccessTokenRemainingExpiryMs(String accessToken) {
        try {
            Claims claims = parseClaims(accessToken);
            Date expiration = claims.getExpiration();
            long now = System.currentTimeMillis();

            return expiration.getTime() - now;
        } catch (Exception e) {
            return 0;
        }
    }

    // 파싱 유틸 메서드
    public Claims parseClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    // Access Token 만료 테스트 전용 메서드
    public String createExpiredAccessToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() - 1000); // 만료

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }

    // Refresh Token 만료 테스트 전용 메서드
    public String createExpiredRefreshToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() - 1000); // 만료

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }
}
