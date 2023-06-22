package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.SensorType;

public record SensorUpdateRequest(
        SensorType type,
        String power,
        String size,
        String productLine,
        float interval,
        String location
        ) {
}
