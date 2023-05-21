package com.capstone.project.domain.user.service;

import com.capstone.project.config.BearerTokenSupplier;
import com.capstone.project.domain.user.controller.payload.UserLoginRequest;
import com.capstone.project.domain.user.controller.payload.UserResponse;
import com.capstone.project.domain.user.controller.payload.UserSignUpRequest;
import com.capstone.project.models.RoleName;
import com.capstone.project.models.User;
import com.capstone.project.repository.RoleRepository;
import com.capstone.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BearerTokenSupplier bearerTokenSupplier;

    @Transactional
    public User signUp(UserSignUpRequest request) {
        this.validateEmail(request);
        User newUser = request.toUser();
        newUser.password(passwordEncoder.encode(request.password()));
        newUser.createdDate(new Date());
        newUser.updatedDate(new Date());
        var initRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("Can't Init Role For User"));
        if(Objects.nonNull(initRole)) {
            newUser.role(initRole);
        }
        return userRepository.save(newUser);
    }

    private void validateEmail(UserSignUpRequest request) {
        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email(`%s`) already exists.".formatted(email));
        }
    }

    @Transactional
    public UserResponse login(UserLoginRequest request) {
        return userRepository
                .findByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.password()))
                .map(user -> {
                    String token = bearerTokenSupplier.supply(user);
                    return new UserResponse(user.token(token));
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

//    @Transactional
//    public UserResponse update(User user, UpdateUserRequest request) {
//        this.updateEmail(user, request);
//        this.updatePassword(user, request);
//        this.updateUsername(user, request);
//        this.updateUserDetails(user, request);
//        return new UserResponse(user);
//    }
//
//    private void updateEmail(User user, UpdateUserRequest request) {
//        String email = request.email();
//        if (email != null && !email.equals(user.email()) && userRepository.existsByEmail(email)) {
//            throw new IllegalArgumentException("Email(`%s`) already exists.".formatted(email));
//        }
//        if (email != null && !email.isBlank()) {
//            user.email(email);
//        }
//    }
//
//    private void updatePassword(User user, UpdateUserRequest request) {
//        String password = request.password();
//        if (password != null && !password.isBlank()) {
//            String encoded = passwordEncoder.encode(password);
//            user.password(encoded);
//        }
//    }
//
//    private void updateUsername(User user, UpdateUserRequest request) {
//        String username = request.username();
//        if (username != null && !username.equals(user.username()) && userRepository.existsByUsername(username)) {
//            throw new IllegalArgumentException("Username(`%s`) already exists.".formatted(username));
//        }
//        if (username != null && !username.isBlank()) {
//            user.username(username);
//        }
//    }
//
//    private void updateUserDetails(User user, UpdateUserRequest request) {
//        user.bio(request.bio());
//        user.image(request.image());
//    }
}

