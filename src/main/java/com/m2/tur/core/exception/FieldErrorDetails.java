package com.m2.tur.core.exception;

public record FieldErrorDetails(
        String field,
        String message
) {}
