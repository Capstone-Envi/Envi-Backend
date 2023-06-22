package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.SensorType;

public record SensorUpdateThresholdRequest(
        float min,
        float max
        ) {
}
