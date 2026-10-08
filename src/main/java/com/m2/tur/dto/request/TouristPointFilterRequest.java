package com.m2.tur.dto.request;

import java.util.UUID;

public record TouristPointFilterRequest(
        String city,

        Long stateId,

        UUID categoryId,

        Long accessibilityId
) {}
