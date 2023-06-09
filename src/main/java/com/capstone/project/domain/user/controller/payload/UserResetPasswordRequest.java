package com.capstone.project.domain.user.controller.payload;

import com.capstone.project.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;


public record UserResetPasswordRequest(
                                String resetPasscode,
                                String newPassword
                                ) {
}
