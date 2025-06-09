package com.woochang.highticket.service.user;

import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("사용자 조회 후 없으면 생성")
    public void findOrCreateUser_notExist_success() {
        // given
        String email = "test@example.com";
        String nickname = "test";
        LoginType loginType = LoginType.GOOGLE;

        User createdUser = User.ofOAuth2(email, nickname, loginType);

        when(userRepository.findByEmailAndLoginType(email, loginType))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenReturn(createdUser);

        // when
        User result = userService.findOrCreateUser(email, nickname, loginType);

        // then
        assertThat(result).isEqualTo(createdUser);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.getLoginType()).isEqualTo(loginType);
    }

    @Test
    @DisplayName("사용자 조회 후 존재하면 반환")
    public void findOrCreateUser_exist_success() {
        // given
        String email = "test@example.com";
        String nickname = "test";
        LoginType loginType = LoginType.GOOGLE;

        User existingUser = User.ofOAuth2(email, nickname, loginType);

        when(userRepository.findByEmailAndLoginType(email, loginType))
                .thenReturn(Optional.of(existingUser));

        // when
        User result = userService.findOrCreateUser(email, nickname, loginType);

        // then
        assertThat(result).isEqualTo(existingUser);
    }
}