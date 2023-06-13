package com.capstone.project.repository;

import com.capstone.project.models.Node;
import com.capstone.project.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NodeRepository extends JpaRepository<Node, UUID> {
    Page<Node> findAllByUsers_Id(UUID userId, Pageable pageable);
    long countByUsers_Id(UUID userId);
}