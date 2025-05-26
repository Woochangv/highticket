package com.woochang.highticket.service.user;

import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.security.CustomOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    @Mock
    UserService userService;

    @InjectMocks
    CustomOAuth2UserService customOAuth2UserService;

    @Test
    @DisplayName("OAuth2 인증 후에 사용자 정보를 로드 후 CustomOAuth2User로 반환")
    public void loadUserByOAuth2User_success() {
        // given
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration registration = mock(ClientRegistration.class);
        when(userRequest.getClientRegistration()).thenReturn(registration);
        when(registration.getRegistrationId()).thenReturn("google");

        Map<String, Object> attributes = Map.of(
                "email", "test@example.com",
                "name", "test"
        );

        OAuth2User mockOAuth2User = mock(OAuth2User.class);
        when(mockOAuth2User.getAttributes()).thenReturn(attributes);
        when(delegate.loadUser(userRequest)).thenReturn(mockOAuth2User);

        // when
        OAuth2User result = customOAuth2UserService.loadUser(userRequest).block();

        // then
        assertThat(result).isInstanceOf(CustomOAuth2User.class);
        verify(userService).findOrCreateUser("test@example.com", "테스트", LoginType.GOOGLE);

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) result;
        assertThat(customOAuth2User.getAttributes().get("email")).isEqualTo("test@example.com");
        assertThat(customOAuth2User.getAttributes().get("nickname")).isEqualTo("테스트");
    }
}