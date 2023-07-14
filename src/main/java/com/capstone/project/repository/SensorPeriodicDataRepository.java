package com.capstone.project.repository;

import com.capstone.project.models.SensorPeriodicData;
import com.capstone.project.models.SensorType;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.UUID;
import java.util.stream.Stream;

public interface SensorPeriodicDataRepository extends JpaRepository<SensorPeriodicData, UUID> {
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
    })
    Stream<SensorPeriodicData> findAllBySensor_IdOrderByCreateTimestampAsc(UUID id);

    SensorPeriodicData findFirstBySensor_IdOrderByCreateTimestampDesc(UUID id);
}
