package com.m2.tur.factory;

import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.model.entity.Address;

import java.time.LocalDateTime;
import java.util.UUID;

public class AddressFactory {
    public static AddressRequest createRequest() {
        return new AddressRequest(
                "Rua do Comércio",
                "Apt 101",
                "Centro",
                "Maceió",
                "57020-000",
                1L
        );
    }

    public static AddressRequest createRequestWithInvalidZipcode() {
        return new AddressRequest(
                "Rua do Comércio",
                null,
                "Centro",
                "Maceió",
                "invalid-zipcode",
                1L
        );
    }

    public static AddressRequest createRequestWithoutRequiredFields() {
        return new AddressRequest(
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static Address createEntity() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setStreet("Rua do Comércio");
        address.setComplement("Apt 101");
        address.setNeighborhood("Centro");
        address.setCity("Maceió");
        address.setZipcode("57020-000");
        address.setLatitude(-9.6658);
        address.setLongitude(-35.7350);
        address.setCreatedAt(LocalDateTime.now());
        address.setState(StateFactory.createEntity());
        return address;
    }
}
