package com.capstone.project.domain.user.controller.payload;

import jakarta.validation.constraints.NotNull;

public record UserActivationRequest(
        @NotNull(message = "isDeleted can't be null")
        Boolean isDeleted) {
}
