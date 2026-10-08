package com.m2.tur.dto.response;

public record AddressResponse(
        String street,

        String complement,

        String neighborhood,

        String city,

        String state,

        String zipcode,

        Double latitude,

        Double longitude
) {}
