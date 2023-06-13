package com.capstone.project.domain.lora.controller;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.lora.controller.payload.NodeResponse;
import com.capstone.project.domain.lora.service.LoraService;
import com.capstone.project.domain.notifications.controller.payload.NotificationResponse;
import com.capstone.project.domain.notifications.service.NotificationService;
import com.capstone.project.models.Node;
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
public class LoraController {
    private final LoraService loraService;
    @GetMapping("/api/lora/nodes")
    public CompletableFuture<ResponseEntity<PaginatedResponse<NodeResponse>>> getNodes(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        PaginationQueryString queryString = new PaginationQueryString(offset, limit);
        return loraService.getNodes(queryString)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/lora/nodes/{id}")
    public CompletableFuture<ResponseEntity<PaginatedResponse<NodeResponse>>> getNodesByUserId(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @PathVariable UUID id) {
        PaginationQueryString queryString = new PaginationQueryString(offset, limit);
        return loraService.getNodesByUserId(queryString, id)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/lora/node/{id}")
    public Node getNode(@PathVariable UUID id) {
        return new Node();
    }

    @PostMapping ("/api/lora/node/{id}")
    public Node createNode(@PathVariable UUID id, @RequestBody Node node) {
        return new Node();
    }

    @PutMapping ("/api/lora/node/{id}")
    public Node updateNode(@PathVariable UUID id, @RequestBody Node node) {
        return new Node();
    }

    @DeleteMapping ("/api/lora/node/{id}")
    public Node deleteNode(@PathVariable UUID id, @RequestBody Node node) {
        return new Node();
    }
}
