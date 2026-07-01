package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressRequest(
        @NotBlank(message = "street required.")
        String street,

        String complement,

        @NotBlank(message = "neighborhood required.")
        String neighborhood,

        @NotBlank(message = "city required.")
        String city,

        @NotBlank(message = "zipcode required.")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$",
        message = "Invalid zipcode format.")
        String zipcode,

        @NotNull
        Long stateId
) {}
