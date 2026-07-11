package com.m2.tur.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

        @Schema(description = "Brazilian zip code in the format XXXXX-XXX.", example = "57020-000")
        @NotBlank(message = "zipcode required.")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$",
        message = "Invalid zipcode format.")
        String zipcode,

        @Schema(description = "ID of the Brazilian state.", example = "1")
        @NotNull
        Long stateId
) {}
