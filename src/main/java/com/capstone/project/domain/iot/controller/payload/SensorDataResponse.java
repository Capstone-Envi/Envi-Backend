package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.SensorIntervalData;
import com.capstone.project.models.SensorPeriodicData;

import java.util.Date;

public record SensorDataResponse(
        double data,
        Date createTimestamp
) {
    public SensorDataResponse(SensorPeriodicData periodSensor) {
        this(
                Math.floor(periodSensor.data() * 100) / (100.0),
                periodSensor.createTimestamp()
        );
    }

    public SensorDataResponse(SensorIntervalData intervalData) {
        this(
                Math.floor(intervalData.data() * 100) / (100.0),
                intervalData.createTimestamp()
        );
    }
}
