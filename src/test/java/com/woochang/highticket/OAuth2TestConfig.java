package com.woochang.highticket;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@TestConfiguration
public class OAuth2TestConfig {


    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegateOAuth2UserService() {
        return new DefaultOAuth2UserService();
    }
}
