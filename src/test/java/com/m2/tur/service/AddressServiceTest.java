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
    class BuildAddress {
        @Test
        void should_return_address_valid_with_success() {
            //Arrange
            AddressRequest request = AddressFactory.createRequest();
            Address address = AddressFactory.createEntity();
            State state = StateFactory.createEntity();
            CoordinatesResponse coordinatesResponse = new CoordinatesResponse(17.405050, 16.324235);

            when(stateService.findEntityById(state.getId())).thenReturn(state);
            when(geocodingClient.getCoordinates(any(String.class))).thenReturn(coordinatesResponse);
            when(addressMapper.toEntity(request, coordinatesResponse.latitude(), coordinatesResponse.longitude())).thenReturn(address);

            //Act & Assert
            var result = assertDoesNotThrow(() -> addressService.buildAddress(request));

            verify(geocodingClient).getCoordinates(any(String.class));

            assertEquals(address.getId(), result.getId());
            assertEquals(state.getId(), result.getState().getId());
        }

        @Test
        void should_throw_geocoding_exception_when_client_fails() {
            //Arrange
            AddressRequest request = AddressFactory.createRequest();
            State state = StateFactory.createEntity();

            when(stateService.findEntityById(state.getId())).thenReturn(state);
            when(geocodingClient.getCoordinates(any(String.class))).thenThrow(new GeocodingException("Failed to retrieve coordinates. Check the address and try again."));

            //Act & Assert
            var result = assertThrows(GeocodingException.class, () -> addressService.buildAddress(request));
            assertEquals("Failed to retrieve coordinates. Check the address and try again.", result.getMessage());
        }
    }
}
