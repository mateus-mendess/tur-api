package com.m2.tur.service;

import com.m2.tur.infra.security.jwt.JwtService;
import com.m2.tur.model.dto.request.AuthenticationRequest;
import com.m2.tur.model.dto.response.AuthenticationResponse;
import com.m2.tur.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        return new AuthenticationResponse(jwtService.generateToken(authentication));
    }

    public Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }

        return Optional.of(userService.findEntityById(UUID.fromString(jwt.getSubject())));
    }
}
