package com.m2.tur.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record ResetPasswordRequest(
        @NotBlank(message = "Token required")
        String token,

        @Size(min = 8, max = 72)
        @NotBlank(message = "Password required")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
        message = "The password must contain at least 8 characters, including uppercase, lowercase, numbers, and special characters")
        String newPassword,

        @Size(min = 8, max = 72)
        @NotBlank(message = "Password confirmation required")
        String confirmNewPassword
) {
    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordConfirmed() {
        return Objects.equals(confirmNewPassword, newPassword);
    }
}
