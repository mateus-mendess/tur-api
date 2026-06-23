package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "")
        @Email(message = "")
        String email,

        @NotBlank(message = "")
        String password
) {}
