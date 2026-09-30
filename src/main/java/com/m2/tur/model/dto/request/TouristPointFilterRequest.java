package com.m2.tur.model.dto.request;

import java.util.UUID;

public record TouristPointFilterRequest(
        String city,

        UUID stateId,

        UUID categoryId,

        UUID accessibilityId
) {}
