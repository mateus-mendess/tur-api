package com.m2.tur.model.dto.response;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name
) {}
