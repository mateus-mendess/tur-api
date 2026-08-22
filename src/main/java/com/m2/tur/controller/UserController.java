package com.m2.tur.controller;

import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.service.TouristPointService;
import com.m2.tur.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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