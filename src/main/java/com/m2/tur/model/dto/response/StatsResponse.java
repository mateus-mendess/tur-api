package com.m2.tur.model.dto.response;

public record StatsResponse(
        long registeredPoints,

        long sharedPhotos,

        long collaborators
) {}
