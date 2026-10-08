package com.m2.tur.dto.response;

import java.util.UUID;

public record PhotoResponse(
        UUID id,
        String url
) {}
