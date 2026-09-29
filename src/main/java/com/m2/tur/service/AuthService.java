package com.m2.tur.service;

import com.m2.tur.config.CookieUtil;
import com.m2.tur.infra.security.jwt.JwtService;
import com.m2.tur.model.dto.request.AuthenticationRequest;
import com.m2.tur.model.dto.response.AuthenticationResponse;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CookieUtil cookieUtil;

    public void authenticate(AuthenticationRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        cookieUtil.createCookie(response, jwtService.generateToken(authentication));
    }

    public void logout(HttpServletResponse response) {
        cookieUtil.clearCookie(response);
    }

    public Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }

        return userRepository.findById(UUID.fromString(jwt.getSubject()));
    }

}
