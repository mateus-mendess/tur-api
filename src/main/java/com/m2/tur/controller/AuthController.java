package com.m2.tur.controller;

import com.m2.tur.model.dto.request.AuthenticationRequest;
import com.m2.tur.model.dto.response.AuthenticationResponse;
import com.m2.tur.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Endpoint for user authentication and JWT token generation.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authenticationService;

    @Operation(summary = "Authenticate user", description = """
            Authenticates a user with email and password.
            Returns a signed JWT token to be used in protected endpoints.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful. Returns a signed JWT token."),
            @ApiResponse(responseCode = "401", description = "Invalid email or password.")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
