package com.m2.tur.factory;

import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.model.entity.AccessibilityTypes;

import java.time.LocalDateTime;

public class AccessibilityTypeFactory {
    public static AccessibilityTypes createEntity() {
        AccessibilityTypes accessibilityType = new AccessibilityTypes();
        accessibilityType.setId(1L);
        accessibilityType.setName("Rampa de acesso");
        accessibilityType.setCreatedAt(LocalDateTime.now());
        return accessibilityType;
    }

    public static AccessibilityTypesResponse createResponse() {
        return new AccessibilityTypesResponse(
                1L,
                "Rampa de acesso"
        );
    }
}
