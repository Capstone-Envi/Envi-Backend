package com.capstone.project.config;

import java.util.UUID;

import com.capstone.project.models.User;
import com.capstone.project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.RequiredArgsConstructor;
@Slf4j
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == User.class;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String userId = jwt.getName();
        String token = jwt.getToken().getTokenValue();

        User user = userRepository
                .findById(UUID.fromString(userId))
                .map(it -> it.token(token))
                .orElseThrow(() -> new BadCredentialsException("Invalid token"));

        if(user.isDeleted()) {
            throw new BadCredentialsException("User is deactivated");
        }
        return user;
    }
}