package com.capstone.project.domain.iot.controller.payload;

import com.capstone.project.models.Node;

import java.util.List;
import java.util.UUID;

public record AssignCustomerToNodeRequest(
        List<UUID> assignCustomerIds
) {
}
