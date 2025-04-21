package com.woochang.highticket.repository.user;

import com.woochang.highticket.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
