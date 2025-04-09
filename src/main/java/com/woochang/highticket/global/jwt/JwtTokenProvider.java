package com.woochang.highticket.global.jwt;

import com.woochang.highticket.global.config.JwtProperties;
import com.woochang.highticket.global.util.PemUtils;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;
    private final UserDetailsService userDetailsService;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtTokenProvider(JwtProperties jwtProperties, UserDetailsService userDetailsService) {
        this.accessTokenExpiryMs = jwtProperties.getAccessTokenExpiryMs();
        this.refreshTokenExpiryMs = jwtProperties.getRefreshTokenExpiryMs();
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

        return Jwts.builder()
                .subject(userId)
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
        String userId = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();

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
            return false;
        }
    }

    // 토큰에서 사용자 ID 추출
    public String getUserId(String jwt) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    // 토큰 만료 시각 추출
    public Date getExpiration(String jwt) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getExpiration();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
