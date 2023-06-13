package com.capstone.project.domain.user.controller.payload;

import jakarta.validation.constraints.Size;

import java.util.Date;

public record UserProfileUpdateRequest(
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
                                String address
                                ) {
}
