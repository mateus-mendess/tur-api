package com.m2.tur.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public record UserRequest(
        @NotBlank(message = "=Name required")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]{2,100}$",
        message = "Name invalid.")
        String name,

        @NotBlank(message = "Email required")
        @Email(message = "Email invalid.")
        String email,

        @Schema(description = "Password must contain at least 8 characters, including uppercase, lowercase, numbers and special characters", example = "Secret@123")
        @NotBlank(message = "Password required")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
        message = "The password must contain at least 8 characters, including uppercase, lowercase, numbers, and special characters")
        String password,

        @NotBlank(message = "Password confirmation required")
        String confirmPassword
) {
        @AssertTrue(message = "Passwords must match")
        public boolean isPasswordConfirmed() {
                return Objects.equals(confirmPassword, password);
        }
}
