package com.m2.tur.model.dto.request;

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
        Boolean hasAccessibility,

        String accessibilityInfo,

        @Valid
        AddressRequest addressRequest,

        @NotNull
        @Size(min = 1,
        message = "You must specify at least one category.")
        Set<UUID> categoriesIds
) {
        @AssertTrue
        boolean isAccessibilityInfoValid() {
                return hasAccessibility == null || !hasAccessibility
                        || (accessibilityInfo != null && !accessibilityInfo.isBlank());
        }
}
