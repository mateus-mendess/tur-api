package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.AssertTrue;

public record TouristPointUpdateRequest(
        String name,

        String description,

        Boolean hasAccessibility,

        String accessibilityInfo
) {
    @AssertTrue
    boolean isAccessibilityInfoValid() {
        return hasAccessibility == null || !hasAccessibility
                || (accessibilityInfo() != null && !accessibilityInfo.isBlank());
    }
}
