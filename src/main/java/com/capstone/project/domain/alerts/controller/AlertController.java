package com.capstone.project.domain.alerts.controller;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.alerts.controller.payload.AlertResponse;
import com.capstone.project.domain.alerts.service.AlertService;
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
public class AlertController {
    private final AlertService alertService;

    @GetMapping("/api/node/{nodeId}/alerts")
    public CompletableFuture<ResponseEntity<PaginatedResponse<AlertResponse>>> getAlertsByNodeId(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @PathVariable UUID nodeId) {
        PaginationQueryString queryString = new PaginationQueryString(offset, limit);

        return alertService.getAlertsByNodeId(queryString, nodeId)
                .thenApply(ResponseEntity::ok);
    }
}
