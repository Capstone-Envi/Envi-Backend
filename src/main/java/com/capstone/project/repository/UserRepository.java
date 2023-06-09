package com.capstone.project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.capstone.project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndExpireResetPasswordTimeAfter(String email, LocalDateTime expireResetPasswordTime);

}