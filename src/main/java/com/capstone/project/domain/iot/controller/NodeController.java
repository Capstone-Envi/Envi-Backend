package com.capstone.project.domain.iot.controller;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.iot.controller.payload.*;
import com.capstone.project.domain.iot.service.NodeService;
import com.capstone.project.domain.user.controller.payload.UserResponse;
import com.capstone.project.domain.user.service.UserService;
import com.capstone.project.models.User;
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
public class NodeController {
    private final NodeService nodeService;
    private final UserService userService;

    @GetMapping("/api/nodes")
    public CompletableFuture<ResponseEntity<PaginatedResponse<NodeResponse>>> getNodes(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        PaginationQueryString queryString = new PaginationQueryString(offset, limit);
        User currentUser = userService.getCurrentUser();
        return nodeService.getNodes(currentUser, queryString)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/node/{id}")
    public CompletableFuture<ResponseEntity<NodeResponse>> getNode(@PathVariable UUID id) {
        return nodeService.getNode(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/api/node")
    public CompletableFuture<ResponseEntity<NodeResponse>> createNode(@RequestBody NodeCreateRequest request) {
        return nodeService.createNode(request)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/api/node/{id}")
    public CompletableFuture<ResponseEntity<NodeResponse>> updateNode(@PathVariable UUID id,
                                                                      @RequestBody NodeUpdateRequest request) {
        return nodeService.updateNode(id, request)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/api/node/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteNode(@PathVariable UUID id) {
        return nodeService.deleteNode(id)
                .thenApply(deleted -> ResponseEntity.noContent().build());
    }

    @PostMapping("/api/node/{nodeId}/assign-users")
    public CompletableFuture<ResponseEntity<NodeWithUsersResponse>> assignUserToNodes(
            @PathVariable UUID nodeId,
            @RequestBody AssignCustomerToNodeRequest request) {
        return nodeService.assignCustomerToNode(nodeId, request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/node/{nodeId}/assign-users")
    public CompletableFuture<ResponseEntity<PaginatedResponse<UserResponse>>> getNodeAssignedUsers(
            @PathVariable UUID nodeId) {
        return nodeService.getNodeAssignedUsers(nodeId)
                .thenApply(ResponseEntity::ok);
    }
}
