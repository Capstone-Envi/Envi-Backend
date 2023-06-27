package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.Sensor;
import com.capstone.project.models.SensorType;

public record SensorCreateRequest(
        String sensorCode,
        float minThreshold,
        float maxThreshold,
        SensorType type,
        String power,
        String size,
        String productLine,
        float interval,
        String location
        ) {
    public Sensor toSensor() {
        return Sensor.builder()
                .sensorCode(sensorCode)
                .minThreshold(minThreshold)
                .maxThreshold(maxThreshold)
                .type(type)
                .power(power)
                .size(size)
                .productLine(productLine)
                .interval(interval)
                .location(location)
                .build();
    }
}
