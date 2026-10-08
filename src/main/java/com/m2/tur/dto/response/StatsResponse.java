package com.m2.tur.dto.response;

public record StatsResponse(
        long registeredPoints,

        long sharedPhotos,

        long collaborators
) {}
