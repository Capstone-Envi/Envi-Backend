package com.capstone.project.domain.user.controller.payload;

import com.capstone.project.models.RoleName;
import com.capstone.project.models.User;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String token,
        String firstName,
        String lastName,
        String phone,
        String address,
        Date dateOfBirth,
        Boolean isDeleted,
        RoleName role,
        LocalDateTime expireResetPasswordTime,
        String resetPasscode,
        String avatar) {
    public UserResponse(User user) {
        this(user.id(),
                user.email(),
                user.token(),
                user.firstName(),
                user.lastName(),
                user.phone(),
                user.address(),
                user.dateOfBirth(),
                user.isDeleted(),
                user.role().getName(),
                user.expireResetPasswordTime(),
                user.resetPasscode(),
                user.avatar());
    }
}
