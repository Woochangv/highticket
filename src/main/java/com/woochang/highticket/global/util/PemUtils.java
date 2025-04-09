package com.woochang.highticket.global.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PemUtils {

    public static PrivateKey readPrivateKey(String path) {
        try {
            String key = readKeyFromFile(path);
            key = key.replaceAll("-----\\w+ PRIVATE KEY-----", "").replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Private Key 읽기에 실패했습니다:  " + path, e);
        }
    }

    public static PublicKey readPublicKey(String path) {
        try {
            String key = readKeyFromFile(path);
            key = key.replaceAll("-----\\w+ PUBLIC KEY-----", "").replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Public Key 읽기에 실패했습니다: " + path, e);
        }
    }

    // 주어진 경로의 키 파일을 읽어서 문자열로 반환
    private static String readKeyFromFile(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }
}
