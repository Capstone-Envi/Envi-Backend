package com.capstone.project.domain.user.service;

import com.capstone.project.config.BearerTokenSupplier;
import com.capstone.project.domain.user.controller.PaginatedResponse;
import com.capstone.project.domain.user.controller.PaginationQueryString;
import com.capstone.project.domain.user.controller.payload.*;
import com.capstone.project.models.RoleName;
import com.capstone.project.models.User;
import com.capstone.project.repository.RoleRepository;
import com.capstone.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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

    @Transactional
    public User update(UUID id, UserProfileUpdateRequest request) {
        User updatedUser = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find user."));

        updatedUser.firstName(request.firstName());
        updatedUser.lastName(request.lastName());
        updatedUser.dateOfBirth(request.dateOfBirth());
        updatedUser.phone(request.phone());

        return userRepository.save(updatedUser);
    }

    @Transactional
    public User create(UserCreateRequest request) {
        User createUser = request.toUser();
        createUser.password(passwordEncoder.encode(createUser.password()));
        return userRepository.save(createUser);
    }

    @Transactional
    public PaginatedResponse<UserResponse> getUsers(PaginationQueryString queryString) {
        Page<User> users = userRepository
                .findAll(queryString.getPageable());

        return new PaginatedResponse<UserResponse>(
                userRepository.count(),
                users.getContent().stream()
                .map(user -> new UserResponse(user))
                .toList()
        );
    }

    @Transactional
    public User getUser(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find user."));
    }

    @Transactional
    public User activation(UUID id, UserActivationRequest request) {
        User updatedUser = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't find user."));

        updatedUser.isDeleted(request.isDeleted());
        return userRepository.save(updatedUser);
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
                .orElseThrow(() -> new BadCredentialsException("Can't find current User"));
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

