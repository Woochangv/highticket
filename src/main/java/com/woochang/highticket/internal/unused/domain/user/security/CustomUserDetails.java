package com.woochang.highticket.internal.unused.domain.user.security;

import com.woochang.highticket.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    }

    @Override
    public String getUsername() {
        return user.getUserIdString();
    }

    @Override
    public String getPassword() {
        return ""; // OAuth 기반이라 패스워드 필요 없음
    }

    public User getUser() {
        return user;
    }
}
