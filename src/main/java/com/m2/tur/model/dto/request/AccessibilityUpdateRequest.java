package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AccessibilityUpdateRequest(
        @NotNull
        Set<Long> accessibilityTypesIds
) {}
