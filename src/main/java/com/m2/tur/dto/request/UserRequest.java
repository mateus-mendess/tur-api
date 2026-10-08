package com.m2.tur.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Objects;

public record UserRequest(
        @Size(min = 2, max = 60)
        @NotBlank(message = "=Name required")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$",
        message = "Name invalid.")
        String name,

        @Size(max = 150)
        @NotBlank(message = "Email required")
        @Email(message = "Email invalid.")
        String email,

        @Schema(description = "Password must contain at least 8 characters, including uppercase, lowercase, numbers and special characters", example = "Secret@123")
        @Size(min = 8, max = 72)
        @NotBlank(message = "Password required")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
        message = "The password must contain at least 8 characters, including uppercase, lowercase, numbers, and special characters")
        String password,

        @Size(min = 8, max = 72)
        @NotBlank(message = "Password confirmation required")
        String confirmPassword
) {
        public UserRequest {
                if (email != null) email = email.trim().toLowerCase();
        }

        @AssertTrue(message = "Passwords must match")
        public boolean isPasswordConfirmed() {
                return Objects.equals(confirmPassword, password);
        }
}
