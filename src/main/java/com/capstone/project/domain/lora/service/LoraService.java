package com.capstone.project.domain.lora.service;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.lora.controller.payload.NodeResponse;
import com.capstone.project.domain.notifications.controller.payload.NotificationResponse;
import com.capstone.project.models.Node;
import com.capstone.project.models.Notification;
import com.capstone.project.repository.NodeRepository;
import com.capstone.project.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoraService {
    private final NodeRepository nodeRepository;
    @Transactional
    @Async
    public CompletableFuture<PaginatedResponse<NodeResponse>> getNodesByUserId(PaginationQueryString queryString, UUID userId) {
        Page<Node> nodes = nodeRepository
                .findAllByUsers_Id(userId, queryString.getPageable());

        return CompletableFuture.completedFuture(new PaginatedResponse<NodeResponse>(
                nodeRepository.countByUsers_Id(userId),
                nodes.getContent().stream().map(NodeResponse::new).toList()
        ));
    }

    @Transactional
    @Async
    public CompletableFuture<PaginatedResponse<NodeResponse>> getNodes(PaginationQueryString queryString) {
//        Pageable page = queryString.getPageable();
        Page<Node> nodes = nodeRepository
                .findAll(queryString.getPageable());

        return CompletableFuture.completedFuture(new PaginatedResponse<NodeResponse>(
                nodeRepository.count(),
                nodes.getContent().stream().map(NodeResponse::new).toList()
        ));
    }
}
