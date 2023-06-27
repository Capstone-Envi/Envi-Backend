package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.Node;

import java.util.Date;
import java.util.UUID;

public record NodeResponse(UUID id, String nodeCode, String name, String description, Date createdDate, Date updatedDate) {
    public NodeResponse(Node node) {
        this(
                node.id(),
                node.nodeCode(),
                node.name(),
                node.description(),
                node.createdDate(),
                node.updatedDate());
    }
}