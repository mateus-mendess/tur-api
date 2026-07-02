package com.m2.tur.model.dto.response;

public record AddressResponse(
        String street,

        String complement,

        String neighborhood,

        String city,

        String state,

        String zipcode
) {}
