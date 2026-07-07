package com.m2.tur.model.dto.response;

import java.util.Set;
import java.util.UUID;

public record TouristPointResponse(
        UUID id,

        String name,

        String description,

        String accessibilityInfo,

        AddressResponse addressResponse,

        Set<PhotoResponse> photoResponses,

        Set<String> categories
) {}
