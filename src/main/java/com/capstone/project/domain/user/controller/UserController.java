package com.capstone.project.domain.user.controller;

import com.capstone.project.domain.user.controller.payload.UserLoginRequest;
import com.capstone.project.domain.user.controller.payload.UserResponse;
import com.capstone.project.domain.user.controller.payload.UserSignUpRequest;
import com.capstone.project.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Map;

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
        log.error("HERE");
        UserResponse response = userService.login(request);
        return response;
    }

    @PostMapping("/api/users/login-test")
    public String login() {
        String response = "132123123";
        return response;
    }

//    @GetMapping("/api/user")
//    public UserResponse getCurrentUser(User me) {
//        UserVO userVO = new UserVO(me);
//        return new UserResponse(userVO);
//    }
//
//    @PutMapping("/api/user")
//    public UserResponse updateCurrentUser(User me, @RequestBody UpdateUserRequest request) {
//        UserVO userVO = userService.update(me, request);
//        return new UserResponse(userVO);
//    }
}

