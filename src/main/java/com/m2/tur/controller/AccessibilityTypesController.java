package com.m2.tur.controller;

import com.m2.tur.model.dto.request.AccessibilityUpdateRequest;
import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.service.AccessibilityTypesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PatchMapping("/tourist-point/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody AccessibilityUpdateRequest request) {
        accessibilityTypesService.update(id, request);

        return ResponseEntity.ok().build();
    }
}

