package com.m2.tur.service;

import com.m2.tur.factory.AddressFactory;
import com.m2.tur.infra.client.GeocodingClient;
import com.m2.tur.infra.exception.GeocodingException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.AddressMapper;
import com.m2.tur.dto.request.AddressRequest;
import com.m2.tur.dto.response.CoordinatesResponse;
import com.m2.tur.model.entity.Address;
import com.m2.tur.model.entity.State;
import com.m2.tur.model.repository.AddressRepository;
import com.m2.tur.model.repository.StateRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
    @Mock
    private GeocodingClient geocodingClient;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private TouristPointRepository touristPointRepository;

    @InjectMocks
    private AddressService addressService;

    @Captor
    private ArgumentCaptor<Address> captor;

    private AddressRequest addressRequest;
    private Address address;
    private State state;

    @BeforeEach
    void setUp() {
        addressRequest = AddressFactory.createRequest();
        address = AddressFactory.createEntity();
        state = address.getState();
    }

    @Nested
    class Create {
        @Test
        void should_create_address_with_success() {
            //Arrange
            when(stateRepository.findById(any(Long.class))).thenReturn(Optional.of(state));
            when(geocodingClient.getCoordinates(any(String.class)))
                    .thenReturn(new CoordinatesResponse(address.getLatitude(), address.getLongitude()));
            when(addressMapper.toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), any(Address.class)))
                    .thenReturn(address);

            //Act & Assert
            var result = addressService.create(addressRequest);

            ArgumentCaptor<Double> latCaptor = ArgumentCaptor.forClass(Double.class);
            ArgumentCaptor<Double> lonCaptor = ArgumentCaptor.forClass(Double.class);

            verify(addressMapper).toEntity(any(AddressRequest.class), latCaptor.capture(), lonCaptor.capture(), any(Address.class));

            assertEquals(state, result.getState());
            assertEquals(address.getLatitude(), latCaptor.getValue());
            assertEquals(address.getLongitude(), lonCaptor.getValue());
        }

        @Test
        void should_throw_not_found_exception_when_no_state_exists() {
            //Arrange
            when(stateRepository.findById(any(Long.class))).thenThrow(NotFoundException.class);

            //Act & Assert
            assertThrows(NotFoundException.class, () -> addressService.create(AddressFactory.createRequest()));

            verifyNoInteractions(addressMapper);
        }

        @Test
        void should_throw_geocoding_exception_when_geocoding_client_fails() {
            //Arrange
            when(stateRepository.findById(state.getId())).thenReturn(Optional.of(state));
            when(geocodingClient.getCoordinates(any(String.class))).thenThrow(GeocodingException.class);

            //Act & Assert
            assertThrows(GeocodingException.class, () -> addressService.create(addressRequest));

            verifyNoInteractions(addressMapper);
        }
    }
}
