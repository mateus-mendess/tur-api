package com.m2.tur.service;

import com.m2.tur.factory.AddressFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.*;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.model.dto.request.TouristPointFilterRequest;
import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.dto.response.TouristPointSummaryResponse;
import com.m2.tur.model.entity.*;
import com.m2.tur.model.repository.AccessibilityTypesRepository;
import com.m2.tur.model.repository.CategoryRepository;
import com.m2.tur.model.repository.CommentRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private TouristPointService touristPointService;

    @Captor
    private ArgumentCaptor<TouristPoint> captor;

    private TouristPointRequest touristPointRequest;
    private TouristPointFilterRequest filterRequest;
    private TouristPoint touristPoint;
    private UUID id;
    private User user;
    private Address address;
    private Set<Category> categories;
    private Set<AccessibilityTypes> accessibilityTypes;
    private TouristPointResponse touristPointResponse;
    private TouristPointSummaryResponse touristPointSummaryResponse;

    @BeforeEach
    void setUp() {
        touristPointRequest = TouristPointFactory.createRequest();
        filterRequest = TouristPointFactory.createFilterRequest();
        touristPoint = TouristPointFactory.createEntity();
        id = touristPoint.getId();
        user = touristPoint.getUser();
        address = touristPoint.getAddress();
        categories = touristPoint.getCategories();
        accessibilityTypes = touristPoint.getAccessibilityTypes();
        touristPointResponse = TouristPointFactory.createResponse();
        touristPointSummaryResponse = TouristPointFactory.createSummaryResponse();
    }

    @Nested
    class FindAll {
        @Test
        void should_return_all_tourist_points_with_success() {
            //Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<TouristPoint> page = new PageImpl<>(List.of(touristPoint), pageable, 10);

            when(touristPointRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(touristPointMapper.toSummaryResponse(touristPoint)).thenReturn(touristPointSummaryResponse);

            //Act & Assert
            var result = touristPointService.findAll(filterRequest, pageable);

            assertNotNull(result);
            assertInstanceOf(TouristPointSummaryResponse.class, result.getContent().get(0));
        }

        @Test
        void should_return_empty_list_when_no_tourist_points_exist() {
            //Arrange
            Pageable pageable = PageRequest.of(0, 10);

            when(touristPointRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

            //Act & Assert
            var result = touristPointService.findAll(filterRequest, pageable);

            verify(touristPointRepository).findAll(any(Specification.class), any(Pageable.class));

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class FindById {
        @Test
        void should_return_tourist_point_with_success() {
            //Arrange
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            when(commentRepository.findAverageRatingByTouristPointId(any(UUID.class))).thenReturn(touristPointResponse.averageRating());
            when(touristPointMapper.toResponse(touristPoint, touristPointResponse.averageRating())).thenReturn(touristPointResponse);

            //Act & Assert
            var result = touristPointService.findById(touristPoint.getId());

            verify(touristPointMapper).toResponse(any(TouristPoint.class), any(Double.class));

            assertEquals(touristPointResponse.id(), result.id());
            assertNotNull(result.averageRating());
        }

        @Test
        void should_throw_not_found_exception_when_not_tourist_point_exist() {
            //Arrange
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> touristPointService.findById(UUID.randomUUID()));
        }
    }

    @Nested
    class FindMyTouristPoints {
        @Test
        void should_return_tourist_points_with_success() {
            //Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<TouristPoint> page = new PageImpl<>(List.of(touristPoint), pageable, 10);

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(touristPointMapper.toSummaryResponse(any(TouristPoint.class))).thenReturn(touristPointSummaryResponse);

            //Act & Assert
            var result = touristPointService.findMyTouristPoints(filterRequest, pageable);

            assertEquals(touristPointSummaryResponse, result.getContent().get(0));
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            Pageable pageable = PageRequest.of(0, 10);

            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> touristPointService.findMyTouristPoints(filterRequest, pageable));
        }
    }

    @Nested
    class Save {
        @Test
        void should_save_tourist_point_with_success() {
            //Arrange
            TouristPoint touristPointEmpty = new TouristPoint();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(addressService.create(touristPointRequest.addressRequest())).thenReturn(address);
            when(categoryRepository.findAllById(touristPointRequest.categoriesIds())).thenReturn(List.copyOf(categories));
            when(accessibilityTypesRepository.findAllById(touristPointRequest.accessibilityTypesIds())).thenReturn(List.copyOf(accessibilityTypes));
            when(touristPointMapper.toEntity(touristPointRequest)).thenReturn(touristPointEmpty);
            when(touristPointMapper.toResponse(touristPointEmpty, 0.0)).thenReturn(touristPointResponse);

            //Act & Assert
            var result = touristPointService.save(touristPointRequest);

            verify(addressService).create(any(AddressRequest.class));
            verify(touristPointRepository).save(captor.capture());

            var captured = captor.getValue();

            assertSame(touristPointResponse, result);
            assertEquals(user, captured.getUser());
            assertEquals(address, captured.getAddress());
            assertEquals(categories, captured.getCategories());
            assertEquals(accessibilityTypes, captured.getAccessibilityTypes());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());
            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> touristPointService.save(touristPointRequest));

            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));
        }

        @Test
        void should_throw_not_found_exception_when_no_states_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            when(addressService.create(any(AddressRequest.class))).thenThrow(new NotFoundException("state not found"));

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.update(id, touristPointRequest));

            verifyNoInteractions(touristPointMapper);

            assertEquals("state not found", result.getMessage());
        }

        @Test
        void should_throw_geocoding_exception_when_GeocodingClient_failed() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(addressService.create(touristPointRequest.addressRequest())).thenThrow(
                    new GeocodingException("Failed to retrieve coordinates. Check the address and try again.")
            );

            //Act & Assert
            assertThrows(GeocodingException.class, () -> touristPointService.save(touristPointRequest));

            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));

        }

        @Test
        void should_throw_not_found_exception_when_no_category_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(addressService.create(touristPointRequest.addressRequest())).thenReturn(AddressFactory.createEntity());
            when(categoryRepository.findAllById(touristPointRequest.categoriesIds())).thenReturn(new ArrayList<>());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.save(touristPointRequest));

            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));

            assertEquals("Category not found.", result.getMessage());
        }

        @Test
        void should_throw_not_found_exception_when_no_accessibility_types_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(addressService.create(touristPointRequest.addressRequest())).thenReturn(AddressFactory.createEntity());
            when(categoryRepository.findAllById(touristPointRequest.categoriesIds())).thenReturn(new ArrayList<>(touristPoint.getCategories()));
            when(accessibilityTypesRepository.findAllById(touristPointRequest.accessibilityTypesIds())).thenReturn(new ArrayList<>());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.save(touristPointRequest));

            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));

            assertEquals("Accessibility not found.", result.getMessage());
        }
    }

    @Nested
    class Update {
        @Test
        void should_update_tourist_point_with_success() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            when(addressService.create(any(AddressRequest.class))).thenReturn(address);
            when(categoryRepository.findAllById(touristPointRequest.categoriesIds())).thenReturn(List.copyOf(categories));
            when(accessibilityTypesRepository.findAllById(touristPointRequest.accessibilityTypesIds())).thenReturn(List.copyOf(accessibilityTypes));

            //Act & Assert
            touristPointService.update(id, touristPointRequest);

            verify(touristPointMapper).updateEntity(touristPointRequest, address, categories, accessibilityTypes, touristPoint);

            assertEquals(touristPoint.getUser(), user);
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> touristPointService.update(UUID.randomUUID(), TouristPointFactory.createRequest()));

            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));
        }

        @Test
        void should_throw_not_found_exception_when_no_tourist_point_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> touristPointService.update(UUID.randomUUID(), TouristPointFactory.createRequest()));

            verify(touristPointRepository).findById(any(UUID.class));
        }

        @Test
        void should_throw_forbidden_exception_when_user_not_authorized() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            assertThrows(ForbiddenException.class, () -> touristPointService.update(id, touristPointRequest));
        }

        @Test
        void should_throw_not_found_exception_when_no_states_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            when(addressService.create(any(AddressRequest.class))).thenThrow(new NotFoundException("state not found"));

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.update(id, touristPointRequest));

            verifyNoInteractions(touristPointMapper);

            assertEquals("state not found", result.getMessage());
        }

        @Test
        void should_throw_geocoding_exception_when_GeocodingClient_failed() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            when(addressService.create(any(AddressRequest.class))).thenThrow(GeocodingException.class);

            //Act & Assert
            assertThrows(GeocodingException.class, () -> touristPointService.update(id, touristPointRequest));

            verifyNoInteractions(touristPointMapper);
        }

        @Test
        void should_throw_not_found_exception_when_no_category_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            when(addressService.create(any(AddressRequest.class))).thenReturn(address);
            when(categoryRepository.findAllById(touristPointRequest.categoriesIds())).thenReturn(Collections.emptyList());
            when(accessibilityTypesRepository.findAllById(touristPointRequest.accessibilityTypesIds())).thenReturn(List.copyOf(accessibilityTypes));

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.update(id, touristPointRequest));

            verifyNoInteractions(touristPointMapper);

            assertEquals("Category not found.",  result.getMessage());
        }

        @Test
        void should_throw_not_found_exception_when_no_accessibility_types_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(addressService.create(touristPointRequest.addressRequest())).thenReturn(AddressFactory.createEntity());
            when(categoryRepository.findAllById(touristPointRequest.categoriesIds())).thenReturn(new ArrayList<>(touristPoint.getCategories()));
            when(accessibilityTypesRepository.findAllById(touristPointRequest.accessibilityTypesIds())).thenReturn(new ArrayList<>());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> touristPointService.save(touristPointRequest));

            verify(touristPointRepository, times(0)).save(any(TouristPoint.class));

            assertEquals("Accessibility not found.", result.getMessage());
        }
    }

    @Nested
    class Delete {
        @Test
        void should_delete_tourist_point_with_success() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            touristPointService.delete(id);

            verify(photoService).deleteByTouristPoint(any(TouristPoint.class));
            verify(touristPointRepository).delete(captor.capture());

            var captured = captor.getValue();

            assertEquals(touristPoint.getUser(), captured.getUser());
            assertEquals(touristPoint.getId(), captured.getId());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> touristPointService.delete(UUID.randomUUID()));

            verify(photoService, times(0)).deleteByTouristPoint(any(TouristPoint.class));
            verify(touristPointRepository, times(0)).delete(any(TouristPoint.class));
        }

        @Test
        void should_throw_not_found_exception_when_no_tourist_point_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> touristPointService.delete(UUID.randomUUID()));
        }

        @Test
        void should_throw_forbidden_exception_when_user_not_authorized() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            assertThrows(ForbiddenException.class, () -> touristPointService.delete(touristPoint.getId()));

            verify(photoService, times(0)).deleteByTouristPoint(any(TouristPoint.class));
            verify(touristPointRepository, times(0)).delete(any(TouristPoint.class));
        }

        @Test
        void should_throw_storage_exception_when_supabase_storage_fails() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(touristPoint));
            doThrow(StorageException.class).when(photoService).deleteByTouristPoint(touristPoint);

            //Act & Assert
            assertThrows(StorageException.class, () -> touristPointService.delete(UUID.randomUUID()));

            verify(touristPointRepository, times(0)).delete(any(TouristPoint.class));
        }
    }
}
