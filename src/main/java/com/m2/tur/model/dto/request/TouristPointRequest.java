package com.m2.tur.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record TouristPointRequest(
        @NotBlank
        @Size(min = 5, max = 100)
        String name,

        @NotBlank
        String description,

        @NotNull
        Set<Long> accessibilityTypesIds,

        @Valid
        AddressRequest addressRequest,

        @Schema(description = "List of category IDs to associate with the tourist point. At least one required.", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
        @NotNull
        @Size(min = 1,
        message = "You must specify at least one category.")
        Set<UUID> categoriesIds
) {}
