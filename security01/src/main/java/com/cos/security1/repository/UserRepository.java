package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Jpa Naming 전략
    // SELECT * FROM user WHERE username = 1?
    User findByUsername(String username);
    // SELECT * FROM user WHERE username = 1? AND password = 2?
    // User findByUsernameAndPassword(String username, String password);

    // @Query(value = "select * from user", nativeQuery = true)
    // User find마음대로();
}