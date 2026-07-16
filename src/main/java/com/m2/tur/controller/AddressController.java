package com.m2.tur.controller;

import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Addresses", description = "Endpoint for updating tourist points addresses")
@RequiredArgsConstructor
@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;

    @Operation(summary = "Updates the addresses of tourist point",
            description = """
                    Updates the address associated with an existing tourist point.
                    The provided ID must belong to an existing tourist point.
                    During the update, the system validates the address and retrieves
                    its geographic coordinates using the geocoding service."
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address updated successfully."),
            @ApiResponse(responseCode = "404", description = "Tourist point not found or State not found."),
            @ApiResponse(responseCode = "503", description = "Failed to retrieve coordinates from geocoding service.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/tourist-point/{id}")
    public ResponseEntity<Void> update(
            @Parameter(description = "The ID is for the tourist point whose address you wish to updated.")
            @PathVariable UUID id,
            @RequestBody @Valid AddressRequest request) {

        addressService.update(id, request);

       return ResponseEntity.ok().build();
    }

}
