package com.capstone.project.domain.notifications.controller.payload;

import com.capstone.project.models.Notification;
import com.capstone.project.models.User;

import java.util.Date;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        boolean isRead,
        String content,
        Date createdDate) {
    public NotificationResponse(Notification notification) {
        this(notification.id(),
                notification.isRead(),
                notification.content(),
                notification.createdDate()
        );
    }
}
