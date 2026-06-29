package com.m2.tur.model.dto.request;

public record AddressRequest(
        String street,
        String neighborhood,
        String city,
        String zipcode,
        Long stateId
) {}
