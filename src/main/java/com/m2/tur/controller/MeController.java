package com.m2.tur.controller;

import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.service.TouristPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/me")
public class MeController {
    private final TouristPointService touristPointService;

    @Operation(summary = "List the authenticated user's tourist points", description = """
            Returns all tourist points registered by the currently authenticated user.
            The user is resolved from the JWT bearer token; no user identifier is
            accepted as a request parameter, preventing access to other users' data.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tourist points retrieved successfully. Returns an empty list if the user has not registered any."),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/tourist-points")
    public ResponseEntity<List<TouristPointResponse>> listMyTouristPoints() {
        return ResponseEntity.ok(touristPointService.findMyTouristPoints());
    }
}
