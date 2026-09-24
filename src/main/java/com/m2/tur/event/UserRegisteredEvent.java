package com.m2.tur.event;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email
) {}
