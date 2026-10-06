package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResendVerificationRequest(
        @Size(max = 150)
        @NotBlank(message = "Email required")
        @Email(message = "Email invalid.")
        String email
) {}
