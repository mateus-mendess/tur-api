package com.m2.tur.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @Size(max = 150)
        @NotBlank
        @Email
        String email
) {
        public  ForgotPasswordRequest {
                if (email != null) email = email.trim().toLowerCase();
        }
}
