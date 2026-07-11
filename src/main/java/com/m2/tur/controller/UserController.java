package com.m2.tur.controller;

import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Endpoint for user registration.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Register user", description = """
            Creates a new user account with name, email and password.
            Returns 201 Created on success.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data or email already in use.")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserRequest request) {
        userService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}