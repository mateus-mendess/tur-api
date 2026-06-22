package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequest(
        @NotBlank(message = "")
        @Email(message = "")
        String email,

        @NotBlank
        @Pattern(regexp = "",
        message = "")
        String password,

        @NotBlank(message = "")
        String confirmPassword
) {}
