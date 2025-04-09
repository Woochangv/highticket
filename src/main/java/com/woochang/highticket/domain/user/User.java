package com.woochang.highticket.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String loginType;
    private Date createdAt;


    public User(String email, String loginType, Date createdAt) {
        this.email = email;
        this.loginType = loginType;
        this.createdAt = createdAt;
    }
}
