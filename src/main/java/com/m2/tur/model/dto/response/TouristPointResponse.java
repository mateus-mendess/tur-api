package com.m2.tur.model.dto.response;

import java.util.Set;
import java.util.UUID;

public record TouristPointResponse(
        UUID id,

        String name,

        String description,

        Set<CategoryResponse> categories,

        Set<AccessibilityTypesResponse> accessibilityTypes,

        AddressResponse address,

        Set<PhotoResponse> photos,

        Double averageRating,

        String userName,

        UUID userId
) {}
