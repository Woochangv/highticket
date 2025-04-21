package com.woochang.highticket.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 10, nullable = false)
    private String loginType;

    @Column(nullable = false)
    private LocalDateTime createdAt;


    public User(String email, String loginType, LocalDateTime createdAt) {
        this.email = email;
        this.loginType = loginType;
        this.createdAt = createdAt;
    }
}
