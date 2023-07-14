package com.capstone.project.domain.alerts.controller.payload;

import com.capstone.project.models.Alert;

import java.text.SimpleDateFormat;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        boolean isRead,
        String content,
        String createdDate) {
    public AlertResponse(Alert alert) {
        this(alert.id(),
                alert.isRead(),
                alert.content(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(alert.createdDate())
        );
    }
}
