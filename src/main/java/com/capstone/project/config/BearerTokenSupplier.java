package com.capstone.project.config;

import java.time.Instant;

import com.capstone.project.domain.user.controller.payload.UserResponse;
import com.capstone.project.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class BearerTokenSupplier {
    private final JwtEncoder jwtEncoder;

    public String supply(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("https://envi-capstone.io")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3000000))
                .subject(user.id().toString())
                .claim("email", user.email())
                .claim("firstName", user.firstName())
                .claim("lastName", user.lastName())
                .claim("isDeleted", user.isDeleted())
                .claim("role", user.role().getName())
                .claim("avatar", user.avatar() != null ? user.avatar() : "")
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        String token = jwtEncoder.encode(parameters).getTokenValue();
        log.info("User id `{}` Bearer Token generated: `{}`", user.id(), token);
        return token;
    }
}