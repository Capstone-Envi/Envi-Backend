package com.capstone.project.domain.user.controller.payload;

import com.capstone.project.models.User;

import java.util.Date;
import java.util.UUID;

public record UserResponse(UUID id, String email, String token, String firstName, String lastName, String phone, Date dateOfBirth, Boolean isDeleted) {
    public UserResponse(User user) {
        this(user.id(),
                user.email(),
                user.token(),
                user.firstName(),
                user.lastName(),
                user.phone(),
                user.dateOfBirth(),
                user.isDeleted());
    }
}
