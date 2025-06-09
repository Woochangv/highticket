package com.woochang.highticket.global.security.oauth2;

import com.woochang.highticket.domain.user.LoginType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class OAuth2Attribute {

    private final String email;
    private final String nickname;
    private final LoginType loginType;

    public static OAuth2Attribute of(String registrationId, Map<String, Object> attributes){
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 로그인 유형: " + registrationId);
        };
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("nickname", nickname);
        map.put("loginType", loginType.name());
        return map;
    }

    private static OAuth2Attribute ofGoogle(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name");
        return new OAuth2Attribute(email, nickname, LoginType.GOOGLE);
    }

    private static OAuth2Attribute ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        return new OAuth2Attribute(email, nickname, LoginType.KAKAO);
    }
}
