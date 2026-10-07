package com.m2.tur.infra.exception;

public record FieldErrorDetails(
        String field,
        String message
) {}
