package com.m2.tur.service;

import com.m2.tur.factory.AccessibilityTypeFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.ForbiddenException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.AccessibilityTypesMapper;
import com.m2.tur.model.dto.request.AccessibilityUpdateRequest;
import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.model.entity.AccessibilityTypes;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.repository.AccessibilityTypesRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AccessibilityTypesServiceTest {
    @Mock
    private AccessibilityTypesRepository accessibilityTypesRepository;

    @Mock
    private AccessibilityTypesMapper accessibilityTypesMapper;

    @Mock
    private TouristPointRepository touristPointRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AccessibilityTypesService accessibilityTypesService;

    @Nested
    class FindAllAccessibilityTypes {
        @Test
        void should_return_all_accessibility_types_with_success() {
            //Arrange
            when(accessibilityTypesRepository.findAll()).thenReturn(List.of(AccessibilityTypeFactory.createEntity()));
            when(accessibilityTypesMapper.toResponse(any(AccessibilityTypes.class))).thenReturn(AccessibilityTypeFactory.createResponse());

            //Act & Assert
            var result = assertDoesNotThrow(() -> accessibilityTypesService.findAllAccessibilityTypes());

            verify(accessibilityTypesRepository).findAll();
            verify(accessibilityTypesMapper).toResponse(any(AccessibilityTypes.class));

            assertInstanceOf(AccessibilityTypesResponse.class, result.get(0));
        }
    }

    @Nested
    class Update {
        @Test
        void should_update_accessibility_types_with_success() {
            //Arrange
            AccessibilityUpdateRequest request = AccessibilityTypeFactory.createUpdateRequest();
            AccessibilityTypes accessibilityTypes = AccessibilityTypeFactory.createEntity();
            TouristPoint touristPoint = TouristPointFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(touristPointRepository.findById(touristPoint.getId())).thenReturn(Optional.of(touristPoint));
            when(accessibilityTypesRepository.findAllById(request.accessibilityTypesIds())).thenReturn(List.of(accessibilityTypes));

            //Act & Assert
            assertDoesNotThrow(() -> accessibilityTypesService.update(touristPoint.getId(), request));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(accessibilityTypesRepository).findAllById(any(Set.class));

            assertEquals(Set.of(accessibilityTypes), touristPoint.getAccessibilityTypes());

        }

        @Test
        void should_throw_unauthorized_exception_when_no_user_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> accessibilityTypesService.update(UUID.randomUUID(), AccessibilityTypeFactory.createUpdateRequest()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository, times(0)).findById(any(UUID.class));
            verify(accessibilityTypesRepository, times(0)).findAllById(any(Set.class));
        }

        @Test
        void should_throw_not_found_exception_when_no_tourist_point_exists() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> accessibilityTypesService.update(UUID.randomUUID(), AccessibilityTypeFactory.createUpdateRequest()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(accessibilityTypesRepository, times(0)).findAllById(any(Set.class));

            assertEquals("Tourist Point not found.", result.getMessage());
        }

        @Test
        void should_throw_forbidden_exception_when_no_user_authorized() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(TouristPointFactory.createEntity()));

            //Act & Assert
            assertThrows(ForbiddenException.class, () -> accessibilityTypesService.update(UUID.randomUUID(), AccessibilityTypeFactory.createUpdateRequest()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(accessibilityTypesRepository, times(0)).findAllById(any(Set.class));
        }

        @Test
        void should_throw_not_found_exception_when_no_accessibility_types_exists() {
            //Arrange
            TouristPoint touristPoint = TouristPointFactory.createEntity();
            AccessibilityTypes accessibilityTypes = AccessibilityTypeFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(touristPoint.getUser()));
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(touristPoint));
            when(accessibilityTypesRepository.findAllById(any(Set.class))).thenReturn(List.of(accessibilityTypes));

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () -> accessibilityTypesService.update(UUID.randomUUID(), AccessibilityTypeFactory.createRequestWithIdsInvalids()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository).findById(any(UUID.class));
            verify(accessibilityTypesRepository).findAllById(any(Set.class));

            assertEquals("Accessibility Types not found.", result.getMessage());

        }
    }


}
