package com.capstone.project.domain.user.controller;

import com.capstone.project.domain.user.controller.payload.*;
import com.capstone.project.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @PostMapping("/api/users/register")
    public ModelAndView signUp(@RequestBody UserSignUpRequest request, HttpServletRequest httpServletRequest) {
        userService.signUp(request);

        // Redirect to login API to automatically login when signup is complete
        UserLoginRequest loginRequest = new UserLoginRequest(request.email(), request.password());
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/api/users/login", "user", Map.of("user", loginRequest));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/users/login")
    public UserResponse login(@RequestBody UserLoginRequest request) {
        UserResponse response = userService.login(request);
        return response;
    }

    @PostMapping("/api/users/login-test")
    public UserResponse login() {
        return new UserResponse(userService.getCurrentUser());
    }

    @GetMapping("/api/user/{id}/profile")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id, @RequestBody UserProfileUpdateRequest request) {
        UserResponse response = new UserResponse(userService.get(id));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/user/{id}/profile")
    public ResponseEntity<UserResponse> updateCurrentUser(@PathVariable UUID id, @RequestBody UserProfileUpdateRequest request) {
        UserResponse response = new UserResponse(userService.update(id, request));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/user/{id}/activation")
    public ResponseEntity<UserResponse> updateUserActivation(@PathVariable UUID id, @RequestBody UserActivationRequest request) {
        UserResponse response = new UserResponse(userService.activation(id, request));
        return ResponseEntity.ok(response);
    }
}

