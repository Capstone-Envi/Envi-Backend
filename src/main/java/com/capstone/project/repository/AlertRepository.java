package com.capstone.project.repository;

import com.capstone.project.models.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    Page<Alert> findAllBySensor_Node_IdOrderByCreatedDateDesc(UUID nodeId, Pageable pageable);
    long countBySensor_Node_id(UUID userId);
}
