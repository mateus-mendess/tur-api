package com.m2.tur.controller;

import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.service.TouristPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Tourist Points", description = "Endpoints for listing, creating, updating and removing tourist points.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/tourist-points")
public class TouristPointController {
    private final TouristPointService touristPointService;

    @Operation(summary = "Lists all registered tourist points.", description = """
            Returns all active tourist points registered in the platform.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tourist points retrieved successfully.")
    })
    @GetMapping
    public ResponseEntity<List<TouristPointResponse>> getTouristPoints() {
        return ResponseEntity.ok(touristPointService.findAll());
    }

    @Operation(summary = "List a tourist point.", description = """
            Returns detailed information about a specific tourist point,
            including address, categories, photos and comments.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tourist point retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "Tourist point not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TouristPointResponse> getTouristPointById(@PathVariable UUID id) {
        return ResponseEntity.ok(touristPointService.findById(id));
    }

    @Operation(summary = "Register a tourist point", description = """
            Creates a new tourist point with address, categories and description.
            Coordinates are automatically retrieved from the address via geocoding.
            Returns 201 Created with the location header pointing to the new resource.
            Requires authentication.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tourist point registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "401", description = "User not authenticated."),
            @ApiResponse(responseCode = "404", description = "State not found"),
            @ApiResponse(responseCode = "503", description = "Failed to retrieve coordinates from geocoding service."),
    })
    @PostMapping
    public ResponseEntity<TouristPointResponse> create(@RequestBody @Valid TouristPointRequest request) {
        TouristPointResponse response = touristPointService.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Updates tourist point information", description = """
            Partially updates the information of a specific tourist point.
            Requires authentication. Only the owner can update.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tourist point updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "401", description = "User not authenticated."),
            @ApiResponse(responseCode = "403", description = "User not allowed to update this tourist point."),
            @ApiResponse(responseCode = "404", description = "Tourist point not found."),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid TouristPointUpdateRequest request) {
        touristPointService.update(request, id);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "remove tourist point", description = """
            Permanently removes a tourist point and all its associated data.
            Requires authentication. Only the owner can remove.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tourist point removed successfully."),
            @ApiResponse(responseCode = "401", description = "User not authenticated."),
            @ApiResponse(responseCode = "403", description = "User not allowed to remove this tourist point."),
            @ApiResponse(responseCode = "404", description = "Tourist point not found.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        touristPointService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
