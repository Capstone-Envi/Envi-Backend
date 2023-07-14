package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.SensorIntervalData;
import com.capstone.project.models.SensorPeriodicData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public record ListSensorListDataResponse(
        List<Double> data,
        List<String> createTimestamp
) {
    public ListSensorListDataResponse() {
        this(
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public void add(SensorPeriodicData periodSensor) {
        this.data.add(Math.floor(periodSensor.data() * 100) / (100.0));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createTimestamp.add(dateFormat.format(periodSensor.createTimestamp()));
    }

    public void add(SensorIntervalData intervalData) {
        this.data.add(Math.floor(intervalData.data() * 100) / (100.0));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createTimestamp.add(dateFormat.format(intervalData.createTimestamp()));
    }
}
