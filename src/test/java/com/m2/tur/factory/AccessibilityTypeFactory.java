package com.m2.tur.factory;

import com.m2.tur.model.dto.request.AccessibilityUpdateRequest;
import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.model.entity.AccessibilityTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class AccessibilityTypeFactory {
    public static AccessibilityUpdateRequest createUpdateRequest () {
        return new AccessibilityUpdateRequest(
                Set.of(1L)
        );
    }

    public static AccessibilityUpdateRequest createRequestWithIdsInvalids() {
        return new AccessibilityUpdateRequest(Set.of(1L, 2L, 3L));
    }

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
