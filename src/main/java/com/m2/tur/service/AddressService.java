package com.m2.tur.service;

import com.m2.tur.infra.client.GeocodingClient;
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
    private final GeocodingClient geocodingService;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final StateService stateService;

    public Address buildAddress(AddressRequest request) throws IOException, InterruptedException {
        State state = stateService.findEntityById(request.stateId());

        String fullAddress = "%s, %s, %s, %s, %s".formatted(
                request.street(),
                request.neighborhood(),
                request.city(),
                state.getAbbreviation(),
                request.zipcode()
        );

        CoordinatesResponse response = geocodingService.getCoordinates(fullAddress);

        Address address = addressMapper.toEntity(request, response.latitude(),  response.longitude());
        address.setState(state);

        return address;
    }
}
