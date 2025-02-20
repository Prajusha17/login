package com.example.loginpage.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loginpage.model.User;

public interface userRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String newUsername);
}