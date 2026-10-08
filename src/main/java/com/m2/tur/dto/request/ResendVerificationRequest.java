package com.m2.tur.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResendVerificationRequest(
        @Size(max = 150)
        @NotBlank(message = "Email required")
        @Email(message = "Email invalid.")
        String email
) {
        public ResendVerificationRequest {
                email = email.trim().toLowerCase();
        }
}
