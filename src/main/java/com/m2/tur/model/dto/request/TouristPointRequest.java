package com.m2.tur.model.dto.request;

import java.util.Set;
import java.util.UUID;

public record TouristPointRequest(
        AddressRequest addressRequest,
        Set<UUID> categoriesIds
) {}
