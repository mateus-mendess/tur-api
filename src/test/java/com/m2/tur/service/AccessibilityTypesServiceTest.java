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
            var result = accessibilityTypesService.findAllAccessibilityTypes();

            assertNotNull(result);
        }
    }
}
