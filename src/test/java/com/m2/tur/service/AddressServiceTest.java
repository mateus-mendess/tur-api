package com.m2.tur.service;

import com.m2.tur.factory.AddressFactory;
import com.m2.tur.factory.StateFactory;
import com.m2.tur.infra.client.GeocodingClient;
import com.m2.tur.infra.exception.GeocodingException;
import com.m2.tur.mapper.AddressMapper;
import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.model.dto.response.CoordinatesResponse;
import com.m2.tur.model.entity.Address;
import com.m2.tur.model.entity.State;
import com.m2.tur.model.repository.AddressRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
    @Mock
    private GeocodingClient geocodingClient;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private StateService stateService;

    @InjectMocks
    private AddressService addressService;

    @Captor
    private ArgumentCaptor<Address> captor;

    @Nested
    class Create {
        @Test
        void should_create_address_with_success() {
            //Arrange
            AddressRequest request = AddressFactory.createRequest();
            CoordinatesResponse coordinatesResponse = new CoordinatesResponse(17.909090, 16.909009);
            State state = StateFactory.createEntity();

            when(stateService.findEntityById(any(Long.class))).thenReturn(state);
            when(geocodingClient.getCoordinates(any(String.class))).thenReturn(coordinatesResponse);
            doAnswer(invocation -> {
                Address address = invocation.getArgument(3);
                address.setNeighborhood(request.neighborhood());
                address.setLatitude(coordinatesResponse.latitude());
                address.setLongitude(coordinatesResponse.longitude());
                return null;
            }).when(addressMapper).toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), any(Address.class));

            //Act & Assert
            var result = assertDoesNotThrow(() -> addressService.create(request));

            verify(stateService).findEntityById(any(Long.class));
            verify(geocodingClient).getCoordinates(any(String.class));
            verify(addressMapper).toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), any(Address.class));

            assertInstanceOf(Address.class, result);
            assertEquals(request.neighborhood(), result.getNeighborhood());
            assertEquals(state, result.getState());
            assertEquals(coordinatesResponse.latitude(), result.getLatitude());
            assertEquals(coordinatesResponse.longitude(), result.getLongitude());
        }

        @Test
        void should_throw_geocoding_exception_when_client_fails() {
            //Arrange
            AddressRequest request = AddressFactory.createRequest();
            State state = StateFactory.createEntity();

            when(stateService.findEntityById(state.getId())).thenReturn(state);
            when(geocodingClient.getCoordinates(any(String.class))).thenThrow(new GeocodingException("Failed to retrieve coordinates. Check the address and try again."));

            //Act & Assert
            var result = assertThrows(GeocodingException.class, () -> addressService.create(request));
            assertEquals("Failed to retrieve coordinates. Check the address and try again.", result.getMessage());
        }
    }
}
