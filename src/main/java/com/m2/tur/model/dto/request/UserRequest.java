package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequest(
        @NotBlank(message = "name required.")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]{2,100}$",
        message = "name invalid.")
        String name,

        @NotBlank(message = "email required.")
        @Email(message = "email invalid.")
        String email,

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
        message = "The password must contain at least 8 characters, including uppercase, lowercase, numbers, and special characters.")
        String password,

        @NotBlank(message = "Password confirmation required.")
        String confirmPassword
) {}
