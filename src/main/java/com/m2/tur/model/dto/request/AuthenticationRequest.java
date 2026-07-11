package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "email requiresd.")
        @Email(message = "email invalid.")
        String email,

        @NotBlank(message = "password required.")
        String password
) {}
