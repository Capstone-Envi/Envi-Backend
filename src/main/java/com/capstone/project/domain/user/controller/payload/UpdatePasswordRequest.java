package com.capstone.project.domain.user.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        String email,
        @NotNull(message = "Current password can't be null")
        String oldPassword,
        @NotNull(message = "New password can't be null")
        @Size(min = 8, message = "New password length must larger than 8")
        String newPassword) {
}
