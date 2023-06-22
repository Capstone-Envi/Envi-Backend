package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.Node;

public record NodeCreateRequest(
        String nodeId,
        String name,
        String description
        ) {
    public Node toNode() {
        return Node.builder()
                .nodeId(nodeId)
                .name(name)
                .description(description)
                .build();
    }
}
