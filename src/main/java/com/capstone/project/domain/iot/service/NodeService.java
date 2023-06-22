package com.capstone.project.domain.iot.service;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.iot.controller.payload.*;
import com.capstone.project.domain.user.controller.payload.UserResponse;
import com.capstone.project.domain.user.service.UserService;
import com.capstone.project.models.*;
import com.capstone.project.repository.NodeRepository;
import com.capstone.project.repository.UserRepository;
import com.capstone.project.utils.ExceptionMessage;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class NodeService {
    private final NodeRepository nodeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Async
    public CompletableFuture<PaginatedResponse<NodeResponse>> getNodes(
            User currentUser,
            PaginationQueryString queryString) {
        if (currentUser.role().getName().equals(RoleName.USER)) {
            Page<Node> nodes = nodeRepository
                    .findAllByUsers_Id_OrderByNodeId(currentUser.id(), queryString.getPageable());

            return CompletableFuture.completedFuture(new PaginatedResponse<>(
                    nodeRepository.countByUsers_Id(currentUser.id()),
                    nodes.getContent().stream().map(NodeResponse::new).toList()
            ));
        }

        Page<Node> nodes = nodeRepository
                .findAll(queryString.getPageable());

        return CompletableFuture.completedFuture(new PaginatedResponse<>(
                nodeRepository.count(),
                nodes.getContent().stream().map(NodeResponse::new).toList()
        ));
    }

    @Transactional(readOnly = true)
    public CompletableFuture<NodeResponse> getNode(UUID id) {
        return CompletableFuture.completedFuture(
                nodeRepository.findById(id)
                        .map(NodeResponse::new)
                        .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NODE_NOT_FOUND))
        );
    }

    @Transactional
    public CompletableFuture<NodeResponse> createNode(NodeCreateRequest request) {
        this.validateNodeId(request.nodeId());
        Node newNode = request.toNode();
        newNode.createdDate(new Date());
        newNode.updatedDate(new Date());
        return CompletableFuture.completedFuture(
                new NodeResponse(nodeRepository.save(newNode))
        );
    }

    @Transactional
    public CompletableFuture<NodeResponse> updateNode(UUID id, NodeUpdateRequest request) {
        Node updatedNode = nodeRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NODE_NOT_FOUND));
        updatedNode.name(request.name());
        updatedNode.description(request.description());
        updatedNode.updatedDate(new Date());
        return CompletableFuture.completedFuture(
                new NodeResponse(nodeRepository.save(updatedNode))
        );
    }

    @Transactional
    public CompletableFuture<NodeWithUsersResponse> assignCustomerToNode(
            UUID nodeId,
            AssignCustomerToNodeRequest request) {
        Node updatedNode = nodeRepository
                .findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NODE_NOT_FOUND));
        List<User> assignUser = userRepository.findAllById(request.assignCustomerIds());
        updatedNode.users().forEach(user -> user.nodes().remove(updatedNode));
        updatedNode.users().clear();
        updatedNode.users().addAll(assignUser);
        assignUser.forEach(user -> user.nodes().add(updatedNode));
        return CompletableFuture.completedFuture(
                new NodeWithUsersResponse(nodeRepository.save(updatedNode))
        );
    }

    @Transactional
    public CompletableFuture<PaginatedResponse<UserResponse>> getNodeAssignedUsers(UUID nodeId) {
        Node existNode = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NODE_NOT_FOUND));

        List<User> users = existNode.users();

        return CompletableFuture.completedFuture(new PaginatedResponse<>(
                users.size(),
                users.stream().map(UserResponse::new).toList()
        ));
    }

    @Transactional
    public CompletableFuture<Void> deleteNode(UUID id) {
        nodeRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    private void validateNodeId(String nodeId) {
        if (nodeRepository.existsByNodeId(nodeId)) {
            throw new IllegalArgumentException("NodeId already exists.");
        }
    }
}
