package com.capstone.project.domain.notifications.service;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.notifications.controller.payload.NotificationResponse;
import com.capstone.project.models.Notification;
import com.capstone.project.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    @Async
    public CompletableFuture<PaginatedResponse<NotificationResponse>> getNotificationsByUserId(PaginationQueryString queryString, UUID userId) {
        Page<Notification> notifications = notificationRepository
                .findAllByUserId(userId, queryString.getPageable());

        return CompletableFuture.completedFuture(new PaginatedResponse<NotificationResponse>(
                notificationRepository.countByUserId(userId),
                notifications.getContent().stream().map(NotificationResponse::new).toList()
        ));
    }
}
