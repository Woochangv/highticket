package com.woochang.highticket.service.user;

import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.domain.user.security.CustomOAuth2User;
import com.woochang.highticket.global.security.oauth2.OAuth2Attribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, attributes);
        log.info("OAuth2 attributes: {}", attributes);
        User user = userService.findOrCreateUser(oAuth2Attribute.getEmail(), oAuth2Attribute.getNickname(), oAuth2Attribute.getLoginType());

        return Mono.just(new CustomOAuth2User(user, oAuth2Attribute.toMap()));
    }

    public CustomOAuth2User loadByUserId(String userId) {
        User user = userService.findById(Long.parseLong(userId));
        return new CustomOAuth2User(user, user.toOAuth2Attribute());
    }
}
