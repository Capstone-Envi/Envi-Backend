package com.capstone.project.domain.alerts.service;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.alerts.controller.payload.AlertResponse;
import com.capstone.project.models.Alert;
import com.capstone.project.repository.AlertRepository;
import org.springframework.transaction.annotation.Transactional;
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
public class AlertService {
    private final AlertRepository alertRepository;

    @Transactional
    public CompletableFuture<PaginatedResponse<AlertResponse>> getAlertsByNodeId(PaginationQueryString queryString, UUID nodeId) {
        Page<Alert> alerts = alertRepository
                .findAllBySensor_Node_IdOrderByCreatedDateDesc(nodeId, queryString.getPageable());

        return CompletableFuture.completedFuture(new PaginatedResponse<AlertResponse>(
                alertRepository.countBySensor_Node_id(nodeId ),
                alerts.getContent().stream().map(AlertResponse::new).toList()
        ));
    }
}
