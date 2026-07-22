package com.m2.tur.controller;

import com.m2.tur.model.dto.request.AccessibilityUpdateRequest;
import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.service.AccessibilityTypesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Accessibility", description = "Endpoint for listing available accessibility types for tourist points.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/accessibility-types")
public class AccessibilityTypesController {
    private final AccessibilityTypesService accessibilityTypesService;

    @Operation(summary = "List all accessibility types", description = """
            Returns all available accessibility types.
            Used to populate accessibility selection in tourist point registration and editing forms.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accessibility types retrieved successfully.")
    })
    @GetMapping
    public ResponseEntity<List<AccessibilityTypesResponse>> getAllAccessibilityTypes() {
        return ResponseEntity.ok(accessibilityTypesService.findAllAccessibilityTypes());
    }

    @Operation(summary = "Update tourist point accessibility types", description = """
            Replaces the accessibility types of a specific tourist point.
            Requires authentication. Only the owner can update.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accessibility types updated successfully."),
            @ApiResponse(responseCode = "401", description = "User not authenticated."),
            @ApiResponse(responseCode = "403", description = "User not allowed to update this tourist point."),
            @ApiResponse(responseCode = "404", description = "Tourist point or accessibility type not found.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/tourist-point/{id}")
    public ResponseEntity<Void> update(
            @Parameter(description = "ID of the tourist point to update accessibility types.", required = true)
            @PathVariable UUID id,
            @RequestBody AccessibilityUpdateRequest request) {
        accessibilityTypesService.update(id, request);

        return ResponseEntity.ok().build();
    }
}

