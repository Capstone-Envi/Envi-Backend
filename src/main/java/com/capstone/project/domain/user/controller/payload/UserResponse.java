package com.capstone.project.domain.user.controller.payload;

import com.capstone.project.models.User;

import java.util.Date;

public record UserResponse(String email, String token, String firstName, String lastName, String phone, Date dateOfBirth) {
    public UserResponse(User user) {
        this(user.email(), user.token(), user.firstName(), user.lastName(), user.phone(), user.dateOfBirth());
    }
}
