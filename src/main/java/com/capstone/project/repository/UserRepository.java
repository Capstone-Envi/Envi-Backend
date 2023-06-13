package com.capstone.project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.capstone.project.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    @Query("SELECT n FROM User n WHERE LOWER(CONCAT(n.email, n.firstName, n.lastName)) LIKE %?1%")
    Page<User> findAllBySearch(String searchText, Pageable pageable);

    @Query("SELECT COUNT(n) FROM User n WHERE CONCAT(n.email, n.firstName, n.lastName) LIKE %?1%")
    long countBySearch(String searchText);
}