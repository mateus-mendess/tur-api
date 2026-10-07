package com.m2.tur.service;

import com.m2.tur.infra.client.GeocodingClient;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.AddressMapper;
import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.model.dto.response.CoordinatesResponse;
import com.m2.tur.model.entity.Address;
import com.m2.tur.model.entity.State;
import com.m2.tur.model.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AddressService {
    private final GeocodingClient geocodingClient;
    private final AddressMapper addressMapper;
    private final StateRepository stateRepository;

    public Address create(AddressRequest request) {
        Address address = new Address();

        State state = stateRepository.findById(request.stateId())
                .orElseThrow(() -> new NotFoundException("state not found"));

        address.setState(state);

        String fullAddress = "%s, %s, %s, %s, %s".formatted(
                request.street(),
                request.neighborhood(),
                request.city(),
                state.getAbbreviation(),
                request.zipcode()
        );

        CoordinatesResponse response = geocodingClient.getCoordinates(fullAddress);

        return addressMapper.toEntity(request, response.latitude(), response.longitude(), address);
    }
}
