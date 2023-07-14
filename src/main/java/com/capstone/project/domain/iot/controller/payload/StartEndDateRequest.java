package com.capstone.project.domain.iot.controller.payload;

import java.util.Date;

public record StartEndDateRequest(
        Date startDate,
        Date endDate
        ) {
}
