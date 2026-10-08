package com.m2.tur.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyCodeRequest(
        @NotBlank(message = "Email required")
        @Email(message = "Email invalid")
        String email,

        @NotBlank(message = "Code required")
        @Size(min = 6, max = 6)
        String code
) {
        public VerifyCodeRequest {
                email = email.trim().toLowerCase();
        }
}
