package io.tronbot.dc.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.tronbot.dc.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(final String username);

    void deleteByUsername(String key);

    @Query("SELECT user.username FROM User user")
    Set<String> findAllUsernames();
}
