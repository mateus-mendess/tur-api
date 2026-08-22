package com.m2.tur.controller;

import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.service.TouristPointService;
import com.m2.tur.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Endpoint for user registration.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final TouristPointService touristPointService;

    @Operation(summary = "List the authenticated user's tourist points", description = """
            Returns all tourist points registered by the currently authenticated user.
            The user is resolved from the JWT bearer token; no user identifier is
            accepted as a request parameter, preventing access to other users' data.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tourist points retrieved successfully. Returns an empty list if the user has not registered any."),
            @ApiResponse(responseCode = "401", description = "Missing, invalid, or expired bearer token.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me/tourist-points")
    public ResponseEntity<List<TouristPointResponse>> getMyTouristPoints() {
        return ResponseEntity.ok(touristPointService.findMyTouristPoints());
    }

    @Operation(summary = "Register user", description = """
            Creates a new user account with name, email and password.
            Returns 201 Created on success.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data or email already in use.")
    })
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid UserRequest request) {
        userService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}