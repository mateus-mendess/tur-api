package com.m2.tur.dto.response;

import java.util.Set;
import java.util.UUID;

public record TouristPointSummaryResponse(
        UUID id,

        String name,

        String city,

        String state,

        Set<PhotoResponse> photoResponses
) {}
