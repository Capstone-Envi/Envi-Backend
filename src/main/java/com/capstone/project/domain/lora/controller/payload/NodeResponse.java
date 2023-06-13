package com.capstone.project.domain.lora.controller.payload;

import com.capstone.project.models.Node;

public record NodeResponse(String name, String location, String description) {
    public NodeResponse(Node node) {
        this(
                node.name(),
                node.location(),
                node.description());
    }
}
