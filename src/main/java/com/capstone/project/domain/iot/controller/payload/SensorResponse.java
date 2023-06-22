package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.Sensor;
import com.capstone.project.models.SensorType;

import java.util.Date;
import java.util.UUID;

public record SensorResponse(
        UUID id,
        String sensorId,
        float minThreshold,
        float maxThreshold,
        SensorType type,
        String power,
        String size,
        String productLine,
        float interval,
        String location,
        Date createdDate,
        Date updatedDate
        ) {
    public SensorResponse(Sensor sensor) {
        this(
                sensor.id(),
                sensor.sensorId(),
                sensor.minThreshold(),
                sensor.maxThreshold(),
                sensor.type(),
                sensor.power(),
                sensor.size(),
                sensor.productLine(),
                sensor.interval(),
                sensor.location(),
                sensor.createdDate(),
                sensor.updatedDate());
    }
}
