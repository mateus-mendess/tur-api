package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record TouristPointRequest(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotBlank
        String accessibilityInfo,

        Boolean hasAccessibility,

        AddressRequest addressRequest,

        @NotNull
        Set<UUID> categoriesIds
) {}
