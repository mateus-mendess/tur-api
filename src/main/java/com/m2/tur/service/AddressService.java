package com.m2.tur.service;

import com.m2.tur.infra.client.GeocodingClient;
import com.m2.tur.infra.exception.GeocodingException;
import com.m2.tur.mapper.AddressMapper;
import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.model.dto.response.CoordinatesResponse;
import com.m2.tur.model.entity.Address;
import com.m2.tur.model.entity.State;
import com.m2.tur.model.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class AddressService {
    private final GeocodingClient geocodingClient;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final StateService stateService;

    public Address buildAddress(AddressRequest request) {
        State state = stateService.findEntityById(request.stateId());

        String fullAddress = "%s, %s, %s, %s, %s".formatted(
                request.street(),
                request.neighborhood(),
                request.city(),
                state.getAbbreviation(),
                request.zipcode()
        );

        try {
            CoordinatesResponse response = geocodingClient.getCoordinates(fullAddress);

            Address address = addressMapper.toEntity(request, response.latitude(),  response.longitude());
            address.setState(state);

            return address;
        } catch (Exception e) {
            throw new GeocodingException("Failed to retrieve coordinates. Check the address and try again.");
        }
    }
}
