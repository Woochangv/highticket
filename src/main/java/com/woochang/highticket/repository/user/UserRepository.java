package com.woochang.highticket.repository.user;

import com.woochang.highticket.domain.user.LoginType;
import com.woochang.highticket.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndLoginType(String email, LoginType loginType);

}
