package com.m2.tur.controller;

import com.m2.tur.model.dto.request.AuthenticationRequest;
import com.m2.tur.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Auth", description = "Endpoint for user authentication and JWT token generation.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Authenticate user", description = """
        Authenticates a user with email and password.
        Upon successful authentication, a signed JWT token is securely set in an HttpOnly cookie 
        to be used for subsequent requests to protected endpoints.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful. The JWT token is set via the Set-Cookie header."),
            @ApiResponse(responseCode = "401", description = "Invalid email or password.")
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AuthenticationRequest request, HttpServletResponse response) {
        authService.authenticate(request, response);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Logs out the current user", description = """
            Processes the user logout by invalidating the current authentication context and clearing the HttpOnly JWT cookie.
            After a successful response, the client will no longer have access to protected resources until a new login is performed.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged out and JWT cookie cleared"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - No active session or invalid token provided")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get current authenticated user", description = """
        Returns the id of the currently authenticated user, resolved
        from the JWT. Used by the frontend to check
        whether a valid session exists without needing to inspect the
        token directly.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated user id returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - No active session or invalid token provided")
    })
    @GetMapping("/me")
    public ResponseEntity<UUID> me() {
        return ResponseEntity.ok().body(authService.getMe());
    }
}
