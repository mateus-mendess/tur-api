package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public record ChangePasswordRequest(
        @NotBlank(message = "Current password required.")
        String currentPassword,

        @NotBlank(message = "New password required")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
                message = "The password must contain at least 8 characters, including uppercase, lowercase, numbers, and special characters.")
        String newPassword,

        @NotBlank(message = "Confirm new password required")
        String confirmNewPassword
) {
    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordConfirmed() {return Objects.equals(confirmNewPassword, newPassword);}
}
