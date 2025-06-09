package com.woochang.highticket.service.user;

import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOrCreateUser(String email, String nickname, LoginType loginType) {
        return userRepository.findByEmailAndLoginType(email, loginType)
                .orElseGet(() -> userRepository.save(User.ofOAuth2(email, nickname, loginType)));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + id));
    }
}
