package com.woochang.highticket.domain.user;

import com.woochang.highticket.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 30, nullable = false)
    private String nickname;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String email, String nickname, LoginType loginType, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.loginType = loginType;
        this.role = role;
    }

    // OAuth2 사용자 생성
    public static User ofOAuth2(String email, String nickname, LoginType loginType) {
        return new User(email, nickname, loginType, Role.USER);
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public String getUserIdString() {
        return id.toString();
    }

    // OAuth2 attributes 생성
    public Map<String, Object> toOAuth2Attribute() {
        return Map.of(
                "email", this.email,
                "nickname", this.nickname,
                "loginType", this.nickname
        );
    }
}
