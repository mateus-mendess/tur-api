package com.m2.tur.service;

import com.m2.tur.factory.AccessibilityTypeFactory;
import com.m2.tur.mapper.AccessibilityTypesMapper;
import com.m2.tur.model.entity.AccessibilityTypes;
import com.m2.tur.model.repository.AccessibilityTypesRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
