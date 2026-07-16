package com.m2.tur.service;

import com.m2.tur.factory.AddressFactory;
import com.m2.tur.factory.StateFactory;
import com.m2.tur.factory.TouristPointFactory;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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

    @Mock
    private TouristPointRepository touristPointRepository;

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
        void should_throw_not_found_exception_when_no_state_exists() {
            //Arrange
            when(stateService.findEntityById(any(Long.class))).thenThrow(new NotFoundException("State not found."));

            //Act & Assert
            assertThrows(NotFoundException.class, () -> addressService.create(AddressFactory.createRequest()));

            verify(stateService).findEntityById(any(Long.class));
            verify(geocodingClient, times(0)).getCoordinates(any(String.class));
            verify(addressMapper, times(0)).toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), any(Address.class));
        }

        @Test
        void should_throw_geocoding_exception_when_geocoding_client_fails() {
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

    @Nested
    class Update {
        @Test
        void should_update_address_with_success() {
            //Arrange
            AddressRequest request = AddressFactory.createRequest();
            Address address = AddressFactory.createEntity();
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(touristPoint));
            when(stateService.findEntityById(any(Long.class))).thenReturn(address.getState());
            when(geocodingClient.getCoordinates(any(String.class))).thenReturn(new CoordinatesResponse(address.getLatitude(), address.getLongitude()));
            doAnswer(invocation -> {
                Address test = new Address();
                test.setNeighborhood(request.neighborhood());
                test.setLatitude(address.getLatitude());
                test.setLongitude(address.getLongitude());
                return null;
            }).when(addressMapper).toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), any(Address.class));

            //Act & Assert
            assertDoesNotThrow(() -> addressService.update(touristPoint.getId(), request));

            verify(touristPointRepository).findById(any(UUID.class));
            verify(stateService).findEntityById(any(Long.class));
            verify(geocodingClient).getCoordinates(any(String.class));
            verify(addressMapper).toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), captor.capture());

            var captured = captor.getValue();

            assertEquals(address.getState(), captured.getState());
            assertEquals(address.getNeighborhood(), captured.getNeighborhood());
            assertEquals(address.getLatitude(), captured.getLatitude());
            assertEquals(address.getLongitude(), captured.getLongitude());
        }

        @Test
        void should_throw_not_found_exception_when_no_tourist_point_exists() {
            //Arrange
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> addressService.update(UUID.randomUUID(), AddressFactory.createRequest()));

            verify(stateService, times(0)).findEntityById(any(Long.class));
            verify(geocodingClient, times(0)).getCoordinates(any(String.class));
            verify(addressMapper, times(0)).toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), any(Address.class));

            assertEquals("tourist point not found", result.getMessage());
        }

        @Test
        void should_throw_not_found_exception_when_no_state_exists() {
            //Arrange
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(TouristPointFactory.createEntity()));
            when(stateService.findEntityById(any(Long.class))).thenThrow(new NotFoundException("State not found."));

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> addressService.update(UUID.randomUUID(), AddressFactory.createRequest()));

            verify(touristPointRepository).findById(any(UUID.class));
            verify(stateService).findEntityById(any(Long.class));
            verify(addressMapper, times(0)).toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), any(Address.class));

            assertEquals("State not found.", result.getMessage());
        }

        @Test
        void should_throw_geocoding_exception_when_geocoding_client_fails() {
            //Arrange
            TouristPoint touristPoint = TouristPointFactory.createEntity();
            Address address = AddressFactory.createEntity();

            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(touristPoint));
            when(stateService.findEntityById(any(Long.class))).thenReturn(address.getState());
            when(geocodingClient.getCoordinates(any(String.class))).thenThrow(new GeocodingException("Failed to retrieve coordinates."));

            //Act & Assert
            assertThrows(GeocodingException.class, () -> addressService.update(touristPoint.getId(), AddressFactory.createRequest()));

            verify(touristPointRepository).findById(any(UUID.class));
            verify(stateService).findEntityById(any(Long.class));
            verify(addressMapper, times(0)).toEntity(any(AddressRequest.class), any(Double.class), any(Double.class), any(Address.class));
        }
    }
}
