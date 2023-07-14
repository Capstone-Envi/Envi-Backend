package com.capstone.project.repository;

import com.capstone.project.models.SensorIntervalData;
import com.capstone.project.models.SensorPeriodicData;
import com.capstone.project.models.SensorType;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

public interface SensorIntervalDataRepository extends JpaRepository<SensorIntervalData, UUID> {
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
    })
    Stream<SensorIntervalData> findAllBySensor_IdOrderByCreateTimestampAsc(UUID id);

    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
    })
    Stream<SensorIntervalData> findAllBySensor_TypeOrderByCreateTimestampAsc(SensorType type);

    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
    })
    Stream<SensorIntervalData> findAllByCreateTimestampBetweenAndSensor_TypeOrderByCreateTimestampAsc(Date start, Date end, SensorType type);

    SensorIntervalData findFirstBySensor_IdOrderByCreateTimestampDesc(UUID id);

    boolean existsByCreateTimestamp(Date time);
}
