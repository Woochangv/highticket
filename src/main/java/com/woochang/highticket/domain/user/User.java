package com.woochang.highticket.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String loginType;
    private LocalDateTime createdAt;


    public User(String email, String loginType, LocalDateTime createdAt) {
        this.email = email;
        this.loginType = loginType;
        this.createdAt = createdAt;
    }
}
