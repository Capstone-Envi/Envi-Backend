package com.capstone.project.repository;

import com.capstone.project.models.Sensor;
import com.capstone.project.models.SensorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {
    boolean existsBySensorCode(String sensorCode);
    Page<Sensor> findAllByOrderBySensorCode(Pageable page);
    Page<Sensor> findByNode_Users_IdAndTypeOrderBySensorCode(UUID userId, SensorType type, Pageable page);
    Page<Sensor> findByNode_Users_IdOrderBySensorCode(UUID userId, Pageable page);
    List<Sensor> findByNode_Id(UUID nodeId);
    Long countByNode_Users_Id(UUID userId);
    Long countByNode_Users_IdAndType(UUID userId, SensorType type);
}
