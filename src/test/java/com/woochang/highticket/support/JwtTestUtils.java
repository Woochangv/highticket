package com.woochang.highticket.support;

import io.jsonwebtoken.Jwts;

import java.security.PrivateKey;
import java.util.Date;

public class JwtTestUtils {

    public static String createExpiredAccessToken(PrivateKey privateKey, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() - 1000);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }

    public static String createExpiredRefreshToken(PrivateKey privateKey, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() - 1000);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }
}
