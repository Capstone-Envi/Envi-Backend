package com.capstone.project.domain.iot.controller.payload;

import java.util.List;

public record SensorDataVer2Response(
        List<double[]> data,
        double min,
        double max,
        double avg
) {
}
