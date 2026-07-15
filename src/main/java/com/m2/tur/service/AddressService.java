package com.m2.tur.service;

import com.m2.tur.infra.client.GeocodingClient;
import com.m2.tur.infra.exception.GeocodingException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.AddressMapper;
import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.model.dto.response.CoordinatesResponse;
import com.m2.tur.model.entity.Address;
import com.m2.tur.model.entity.State;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.repository.AddressRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AddressService {
    private final GeocodingClient geocodingClient;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final StateService stateService;
    private final TouristPointRepository touristPointRepository;

    public Address create(AddressRequest request) {
        Address address = new Address();

        fillAddress(request, address);

        return address;
    }

    public void update(UUID touristPointId, AddressRequest request) {
        TouristPoint touristPoint = touristPointRepository.findById(touristPointId)
                .orElseThrow(() -> new NotFoundException("tourist point not found"));

        fillAddress(request, touristPoint.getAddress());
    }

    private void fillAddress(AddressRequest request, Address address) {
        State state = stateService.findEntityById(request.stateId());

        String fullAddress = "%s, %s, %s, %s, %s".formatted(
                request.street(),
                request.neighborhood(),
                request.city(),
                state.getAbbreviation(),
                request.zipcode()
        );

        CoordinatesResponse response = geocodingClient.getCoordinates(fullAddress);

        addressMapper.toEntity(request, response.latitude(), response.longitude(), address);
        address.setState(state);
    }
}
