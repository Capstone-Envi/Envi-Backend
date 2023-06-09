package com.capstone.project.domain.user.service;

import com.capstone.project.config.BearerTokenSupplier;
import com.capstone.project.domain.user.controller.PaginatedResponse;
import com.capstone.project.domain.user.controller.PaginationQueryString;
import com.capstone.project.domain.user.controller.payload.*;
import com.capstone.project.models.RoleName;
import com.capstone.project.models.User;
import com.capstone.project.repository.RoleRepository;
import com.capstone.project.repository.UserRepository;
import com.capstone.project.service.EmailService;
import com.capstone.project.utils.PasswordResetCodeGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final BearerTokenSupplier bearerTokenSupplier;

    private static final String USER_NOT_FOUND = "User not found!";
    private static final String INVALID_EMAIL_PASSWORD = "Invalid email or password!";
    private static final String DISABLED_USER = "User is disabled.";

    @Transactional
    public User signUp(UserSignUpRequest request) {
        this.validateEmail(request.email());
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

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }
    }

    @Transactional
    public UserResponse login(UserLoginRequest request) {
        return userRepository
                .findByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.password()))
                .map(user -> {
                    if (user.isDeleted()) {
                        throw new IllegalArgumentException(USER_NOT_FOUND);
                    }
                    String token = bearerTokenSupplier.supply(user);
                    return new UserResponse(user.token(token));
                })
                .orElseThrow(() -> new IllegalArgumentException(INVALID_EMAIL_PASSWORD));
    }

    @Transactional
    public User update(UUID id, UserProfileUpdateRequest request) {
        User updatedUser = userRepository
                .findById(id)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        updatedUser.firstName(request.firstName());
        updatedUser.lastName(request.lastName());
        updatedUser.dateOfBirth(request.dateOfBirth());
        updatedUser.phone(request.phone());
        return userRepository.save(updatedUser);
    }

    @Transactional
    public User create(UserCreateRequest request) {
        this.validateEmail(request.email());
        User newUser = request.toUser();
        newUser.password(passwordEncoder.encode(request.password()));
        newUser.createdDate(new Date());
        newUser.updatedDate(new Date());
        newUser.isDeleted(false);
        var initRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("Can't Init Role For User"));
        if(Objects.nonNull(initRole)) {
            newUser.role(initRole);
        }
        return userRepository.save(newUser);
    }

    @Transactional
    public PaginatedResponse<UserResponse> getUsers(PaginationQueryString queryString) {
        Page<User> users = userRepository
                .findAll(queryString.getPageable());

        return new PaginatedResponse<UserResponse>(
                userRepository.count(),
                users.getContent().stream()
                .map(UserResponse::new)
                .toList()
        );
    }

    @Transactional
    public User getUser(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
    }

    @Transactional
    public User activation(UUID id, UserActivationRequest request) {
        User updatedUser = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        updatedUser.isDeleted(request.isDeleted());
        return userRepository.save(updatedUser);
    }

    @Transactional
    @Async
    public CompletableFuture<User> sendResetPasswordCode(String email) {
        User sendResetUser = userRepository
                .findByEmail(email)
                .map(user -> {
                    if (user.isDeleted()) {
                        throw new IllegalArgumentException(DISABLED_USER);
                    }
                    String passwordResetCode = PasswordResetCodeGenerator.generateResetCode();
                    LocalDateTime expireTime = LocalDateTime.now().plusMinutes(1);
                    user.expireResetPasswordTime(expireTime);
                    user.resetPasscode(passwordResetCode);
                    emailService.sendSimpleMessage(
                            user.email(),
                            "RESET PASSWORD CODE",
                            "Reset code: " + passwordResetCode);
                    return user;
                })
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        return CompletableFuture.completedFuture(userRepository.save(sendResetUser));
    }

    @Transactional
    @Async
    public CompletableFuture<User> resetPassword(UserResetPasswordRequest request) {
        User resetPasswordUser = userRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        if(resetPasswordUser.isDeleted()) {
            throw new IllegalArgumentException(DISABLED_USER);
        }
        if(LocalDateTime.now().isAfter(resetPasswordUser.expireResetPasswordTime())) {
            throw new IllegalArgumentException("Expired passcode.");
        }
        if(!resetPasswordUser.resetPasscode().equals(request.resetPasscode())){
            throw new IllegalArgumentException("Passcode doesn't match.");
        }
        resetPasswordUser.expireResetPasswordTime(null);
        resetPasswordUser.resetPasscode(null);
        resetPasswordUser.password(passwordEncoder.encode(request.newPassword()));
        return CompletableFuture.completedFuture(userRepository.save(resetPasswordUser));
    }

    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String userId = jwt.getName();
        return userRepository
                .findById(UUID.fromString(userId))
                .orElseThrow(() -> new BadCredentialsException(USER_NOT_FOUND));
    }

//
//    private void updatePassword(User user, UpdateUserRequest request) {
//        String password = request.password();
//        if (password != null && !password.isBlank()) {
//            String encoded = passwordEncoder.encode(password);
//            user.password(encoded);
//        }
//    }
}

