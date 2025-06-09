package com.woochang.highticket.domain.user.security;

import com.woochang.highticket.domain.user.Role;
import com.woochang.highticket.domain.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    @Getter
    private final User user;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.USER.getAuthority()));
    }

    @Override
    public String getName() {
        return user.getUserIdString();
    }

}
