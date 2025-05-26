package com.woochang.highticket.repository.user;

import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.global.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.ofOAuth2("test@example.com", "test", LoginType.GOOGLE);
    }

    @Test
    @DisplayName("사용자 저장 및 매핑 검증")
    public void saveUser_success() {
        // given - init

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("test");
        assertThat(savedUser.getLoginType()).isEqualTo(LoginType.GOOGLE);
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    public void findUser_byEmail_success() {
        // given
        User savedUser = userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("이메일과 로그인 타입으로 사용자 조회")
    public void findUser_byEmailAndLoginType_success() {
        // given
        User savedUser = userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByEmailAndLoginType(savedUser.getEmail(), savedUser.getLoginType());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
    }
    
    @Test
    @DisplayName("사용자 수정 시 updatedAt 시간 갱신 확인")        
    public void updateUser_updatesUpdatedAt_success() throws InterruptedException {
        // given 
        User savedUser = userRepository.save(user);
        userRepository.flush();
        LocalDateTime firstUpdatedAt = savedUser.getUpdatedAt();

        // when
        Thread.sleep(100);
        savedUser.changeNickname("newNickname");
        userRepository.flush();

        // then
        assertThat(savedUser.getUpdatedAt()).isAfter(firstUpdatedAt);
    }

    @Test
    @DisplayName("이메일 누락 시 저장 예외 발생")
    public void emailIsNull_throwException () {
        // given
        User invalidUser = User.ofOAuth2(null, "testNickname", LoginType.GOOGLE);

        // when & then
        assertThatThrownBy(() -> userRepository.save(invalidUser)).isInstanceOf(DataIntegrityViolationException.class);
    }
}