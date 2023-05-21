package com.capstone.project.domain.user.controller.payload;

import com.capstone.project.models.User;

public record UserSignUpRequest(String email, String password, String firstName, String lastName) {
    public User toUser() {
        return User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
