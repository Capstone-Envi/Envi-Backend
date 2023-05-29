package com.capstone.project.domain.user.controller.payload;

import com.capstone.project.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record UserCreateRequest(
                                @Size(min = 6, message = "phone length must larger than 6")
                                @Size(max = 30, message = "phone length must less than 30")
                                String firstName,
                                @Size(min = 6, message = "phone length must larger than 6")
                                @Size(max = 30, message = "phone length must less than 30")
                                String lastName,
                                @Size(min = 2, message = "phone length must larger than 2")
                                @Size(max = 15, message = "phone length must less than 15")
                                String phone,
                                Date dateOfBirth,
                                @NotNull(message = "email can't be null")
                                @Email(message = "Wrong email format")
                                String email,
                                @NotNull(message = "password can't be null")
                                @Size(min = 8, message = "password length must larger than 8")
                                String password
                                ) {
    public User toUser() {
        return User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .build();
    }
}
