package com.capstone.project.domain.notifications.controller;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.notifications.controller.payload.NotificationResponse;
import com.capstone.project.domain.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/api/notifications/{id}")
    public CompletableFuture<ResponseEntity<PaginatedResponse<NotificationResponse>>> getNotificationsByUserId(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @PathVariable UUID id) {
        PaginationQueryString queryString = new PaginationQueryString(offset, limit);

        return notificationService.getNotificationsByUserId(queryString, id)
                .thenApply(ResponseEntity::ok);
    }
}
