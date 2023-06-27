package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.domain.user.controller.payload.UserResponse;
import com.capstone.project.models.Node;
import com.capstone.project.models.User;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record NodeWithUsersResponse(UUID id, String nodeCode, String name,
                                    String description, Date createdDate,
                                    Date updatedDate, List<UserResponse> users) {
    public NodeWithUsersResponse(Node node) {
        this(
                node.id(),
                node.nodeCode(),
                node.name(),
                node.description(),
                node.createdDate(),
                node.updatedDate(),
                node.users().stream().map(UserResponse::new).toList());
    }
}