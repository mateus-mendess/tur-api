package com.m2.tur.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @Size(min = 5, max = 60)
        @NotBlank(message = "street required.")
        String street,

        @Size(min = 5, max = 40)
        String complement,

        @Size(min = 5, max = 60)
        @NotBlank(message = "neighborhood required.")
        String neighborhood,

        @Size(min = 3, max = 60)
        @NotBlank(message = "city required.")
        String city,

        @Schema(description = "Brazilian zip code in the format XXXXX-XXX.", example = "57020-000")
        @Size(min = 9, max = 9)
        @NotBlank(message = "zipcode required.")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$",
        message = "Invalid zipcode format.")
        String zipcode,

        @Schema(description = "ID of the Brazilian state.", example = "1")
        @NotNull
        Long stateId
) {}
