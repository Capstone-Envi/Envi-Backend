package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.Node;

public record NodeCreateRequest(
        String nodeCode,
        String name,
        String description
        ) {
    public Node toNode() {
        return Node.builder()
                .nodeCode(nodeCode)
                .name(name)
                .description(description)
                .build();
    }
}
