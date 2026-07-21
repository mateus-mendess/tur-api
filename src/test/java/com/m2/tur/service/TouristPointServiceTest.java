package com.m2.tur.service;

import com.m2.tur.factory.AddressFactory;
import com.m2.tur.factory.PhotoFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.*;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.AccessibilityTypesRepository;
import com.m2.tur.model.repository.CategoryRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TouristPointServiceTest {
    @Mock
    private TouristPointRepository touristPointRepository;

    @Mock
    private TouristPointMapper touristPointMapper;

    @Mock
    private AuthService authService;

    @Mock
    private AddressService addressService;

    @Mock
    private PhotoService photoService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AccessibilityTypesRepository accessibilityTypesRepository;

    @InjectMocks
    private TouristPointService touristPointService;

    @Captor
    private ArgumentCaptor<TouristPoint> captor;

    @Nested
    class FindAll {
        @Test
        void should_return_all_tourist_points_with_success() {
            //Arrange
            TouristPoint touristPoint = TouristPointFactory.createEntity();
            TouristPointResponse response = TouristPointFactory.createResponse();

            when(touristPointRepository.findAll()).thenReturn(List.of(touristPoint));
            when(touristPointMapper.toResponse(touristPoint)).thenReturn(response);

            //Act & Assert
            var result = assertDoesNotThrow(() -> touristPointService.findAll());

            verify(touristPointRepository).findAll();
            verify(touristPointMapper).toResponse(any(TouristPoint.class));

            assertNotNull(result);
            assertInstanceOf(TouristPointResponse.class, result.get(0));
        }

        @Test
        void should_return_empty_list_when_no_tourist_points_exist() {
            //Arrange
            when(touristPointRepository.findAll()).thenReturn(Collections.emptyList());

            //Act & Assert
            var result = assertDoesNotThrow(() -> touristPointService.findAll());

            verify(touristPointRepository).findAll();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class FindById {
        @Test
        void should_return_tourist_point_with_success() {
            //Arrange
            TouristPoint touristPoint = TouristPointFactory.createEntity();
            TouristPointResponse response = TouristPointFactory.createResponse();

            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            when(touristPointMapper.toResponse(touristPoint)).thenReturn(response);

            //Act & Assert
            var result = assertDoesNotThrow(() -> touristPointService.findById(touristPoint.getId()));

            verify(touristPointRepository).findById(any(UUID.class));

            assertInstanceOf(TouristPointResponse.class, result);
            assertEquals(response.id(), result.id());
        }

        @Test
        void should_throw_not_found_exception_when_not_tourist_point_exist() {
            //Arrange
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> touristPointService.findById(UUID.randomUUID()));

            verify(touristPointMapper, times(0)).toResponse(any(TouristPoint.class));
        }
    }

    @Nested
    class Save {
        @Test
        void should_save_tourist_point_with_success() {
            //Arrange
            TouristPointRequest request = TouristPointFactory.createRequest();
            TouristPoint touristPoint = TouristPointFactory.createEntity();
            TouristPointResponse response = TouristPointFactory.createResponse();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(addressService.create(request.addressRequest())).thenReturn(touristPoint.getAddress());
            when(categoryRepository.findAllById(request.categoriesIds())).thenReturn(new ArrayList<>(touristPoint.getCategories()));
            when(accessibilityTypesRepository.findAllById(request.accessibilityTypesIds())).thenReturn(new ArrayList<>(touristPoint.getAccessibilityTypes()));
            when(touristPointMapper.toEntity(request)).thenReturn(touristPoint);
            when(touristPointRepository.save(touristPoint)).thenReturn(touristPoint);
            when(touristPointMapper.toResponse(touristPoint)).thenReturn(response);

            //Act & Assert
            var result = assertDoesNotThrow(() -> touristPointService.save(request));

            verify(authService).getAuthenticatedUser();
            verify(addressService).create(any(AddressRequest.class));
            verify(categoryRepository).findAllById(any(Set.class));
            verify(accessibilityTypesRepository).findAllById(any(Set.class));
            verify(touristPointMapper).toEntity(any(TouristPointRequest.class));
            verify(touristPointRepository).save(captor.capture());

            var captured = captor.getValue();

            assertInstanceOf(TouristPointResponse.class, result);
            assertEquals(response.id(), result.id());
            assertEquals(touristPoint.getUser(), captured.getUser());
            assertEquals(touristPoint.getAddress(), captured.getAddress());
            assertEquals(touristPoint.getCategories(), captured.getCategories());
            assertEquals(touristPoint.getAccessibilityTypes(), captured.getAccessibilityTypes());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            TouristPointRequest request = TouristPointFactory.createRequest();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());
            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> touristPointService.save(request));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));
        }

        @Test
        void should_throw_geocoding_exception_when_GeocodingClient_failed() {
            //Arrange
            TouristPointRequest request = TouristPointFactory.createRequest();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(addressService.create(request.addressRequest())).thenThrow(new GeocodingException("Failed to retrieve coordinates. Check the address and try again."));

            //Act & Assert
            assertThrows(GeocodingException.class, () -> touristPointService.save(request));

            verify(authService).getAuthenticatedUser();
            verify(addressService).create(any(AddressRequest.class));
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));

        }

        @Test
        void should_throw_not_found_exception_when_no_category_exists() {
            //Arrange
            TouristPointRequest request = TouristPointFactory.createRequest();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(addressService.create(request.addressRequest())).thenReturn(AddressFactory.createEntity());
            when(categoryRepository.findAllById(request.categoriesIds())).thenReturn(new ArrayList<>());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.save(request));

            verify(authService).getAuthenticatedUser();
            verify(addressService).create(any(AddressRequest.class));
            verify(categoryRepository).findAllById(any(Set.class));
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));

            assertEquals("Category not found.", result.getMessage());
        }

        @Test
        void should_throw_not_found_exception_when_no_accessibility_types_exists() {
            //Arrange
            TouristPointRequest request = TouristPointFactory.createRequest();
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(addressService.create(request.addressRequest())).thenReturn(AddressFactory.createEntity());
            when(categoryRepository.findAllById(request.categoriesIds())).thenReturn(new ArrayList<>(touristPoint.getCategories()));
            when(accessibilityTypesRepository.findAllById(request.accessibilityTypesIds())).thenReturn(new ArrayList<>());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.save(request));

            verify(authService).getAuthenticatedUser();
            verify(addressService).create(any(AddressRequest.class));
            verify(categoryRepository).findAllById(any(Set.class));
            verify(accessibilityTypesRepository).findAllById(any(Set.class));
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));

            assertEquals("Accessibility not found.", result.getMessage());
        }
    }

    @Nested
    class Update {
        @Test
        void should_update_tourist_point_with_success() {
            //Arrange
            TouristPointUpdateRequest request = TouristPointFactory.createUpdateRequest();
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            doNothing().when(touristPointMapper).updateEntity(request, touristPoint);
            when(touristPointRepository.save(touristPoint)).thenReturn(touristPoint);

            //Act & Assert
            assertDoesNotThrow(() -> touristPointService.update(request, touristPoint.getId()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointMapper).updateEntity(any(TouristPointUpdateRequest.class), any(TouristPoint.class));
            verify(touristPointRepository).save(captor.capture());

            var captured = captor.getValue();

            assertNotNull(captured);
            assertEquals(touristPoint.getUser(), captured.getUser());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> touristPointService.update(TouristPointFactory.createUpdateRequest(), UUID.randomUUID()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));
        }

        @Test
        void should_throw_not_found_exception_when_no_tourist_point_exists() {
            //Arrange
            UUID id = UUID.randomUUID();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findById(id)).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> touristPointService.update(TouristPointFactory.createUpdateRequest(), id));

            verify(touristPointRepository).findById(any(UUID.class));
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));
        }

        @Test
        void should_throw_forbidden_exception_when_user_not_authorized() {
            //Arrange
            User user =  UserFactory.createEntity();
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            assertThrows(ForbiddenException.class, () -> touristPointService.update(TouristPointFactory.createUpdateRequest(), touristPoint.getId()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(touristPoint.getId());
            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));

            assertNotEquals(touristPoint.getUser(), user);
        }
    }

    @Nested
    class Delete {
        @Test
        void should_delete_tourist_point_with_success() {
            //Arrange
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            doNothing().when(photoService).delete(any(UUID.class));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            assertDoesNotThrow(() -> touristPointService.delete(touristPoint.getId()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(photoService).delete(any(UUID.class));
            verify(touristPointRepository).delete(captor.capture());

            var captured = captor.getValue();

            assertEquals(touristPoint.getUser(), captured.getUser());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> touristPointService.delete(UUID.randomUUID()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository, times(0)).delete(any(TouristPoint.class));
        }

        @Test
        void should_throw_not_found_exception_when_no_tourist_point_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.delete(UUID.randomUUID()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(photoService, times(0)).delete(any(UUID.class));
            verify(touristPointRepository, times(0)).delete(any(TouristPoint.class));

            assertEquals("Tourist Point not found", result.getMessage());
        }

        @Test
        void should_throw_forbidden_exception_when_user_not_authorized() {
            //Arrange
            User user =  UserFactory.createEntity();
            TouristPoint touristPoint =  TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            assertThrows(ForbiddenException.class, () -> touristPointService.delete(touristPoint.getId()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(touristPointRepository, times(0)).delete(any(TouristPoint.class));

            assertNotEquals(touristPoint.getUser(), user);
        }

        @Test
        void should_throw_not_found_exception_when_not_photo_exists() {
            //Arrange
            TouristPoint touristPoint  =  TouristPointFactory.createEntity();
            touristPoint.setPhotos(Set.of(PhotoFactory.createEntity()));

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(touristPoint));
            doThrow(new NotFoundException("Photo not found.")).when(photoService).delete(any(UUID.class));

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.delete(UUID.randomUUID()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(photoService).delete(any(UUID.class));
            verify(touristPointRepository, times(0)).delete(any(TouristPoint.class));

            assertEquals("Photo not found.", result.getMessage());
        }

        @Test
        void should_throw_storage_exception_when_supabase_storage_fails() {
            //Arrange
            TouristPoint touristPoint =  TouristPointFactory.createEntity();
            touristPoint.setPhotos(Set.of(PhotoFactory.createEntity()));

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(touristPoint));
            doThrow(StorageException.class).when(photoService).delete(any(UUID.class));

            //Act & Assert
            assertThrows(StorageException.class, () -> touristPointService.delete(UUID.randomUUID()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(photoService).delete(any(UUID.class));
            verify(touristPointRepository, times(0)).delete(any(TouristPoint.class));
        }
    }
}
