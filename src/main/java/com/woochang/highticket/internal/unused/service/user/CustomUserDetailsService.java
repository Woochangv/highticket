package com.woochang.highticket.internal.unused.service.user;

import com.woochang.highticket.domain.user.User;
import com.woochang.highticket.internal.unused.domain.user.security.CustomUserDetails;
import com.woochang.highticket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

//@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        long id = Long.parseLong(userId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return new CustomUserDetails(user);
    }
}

