package com.m2.tur.factory;

import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.TouristPoint;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class TouristPointFactory {
    public static TouristPointRequest createRequest() {
        return new TouristPointRequest(
                "Praia do Francês",
                "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.",
                Set.of(1L),
                AddressFactory.createRequest(),
                Set.of(UUID.randomUUID())
        );
    }

    public static TouristPointRequest createRequestWithoutCategories() {
        return new TouristPointRequest(
                "Praia do Francês",
                "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.",
                Set.of(1L),
                AddressFactory.createRequest(),
                Collections.emptySet()
        );
    }

    public static TouristPointRequest createRequestWithoutAccessibilityTypes() {
        return new TouristPointRequest(
                "Praia do Francês",
                "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.",
                Collections.emptySet(),
                AddressFactory.createRequest(),
                Set.of(UUID.randomUUID())
        );
    }

    public static TouristPointUpdateRequest createUpdateRequest() {
        return new TouristPointUpdateRequest(
                "Praia do Francês",
                "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca."
        );
    }

    public static TouristPoint createEntity() {
        TouristPoint touristPoint = new TouristPoint();
        touristPoint.setId(UUID.randomUUID());
        touristPoint.setName("Praia do Francês");
        touristPoint.setDescription("Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.");
        touristPoint.setActive(true);
        touristPoint.setCreatedAt(LocalDateTime.now());
        touristPoint.setUser(UserFactory.createEntity());
        touristPoint.setAddress(AddressFactory.createEntity());
        touristPoint.setCategories(Set.of(CategoryFactory.createEntity()));
        touristPoint.setAccessibilityTypes(Set.of(AccessibilityTypeFactory.createEntity()));
        return touristPoint;
    }

    public static TouristPoint createInactiveEntity() {
        TouristPoint touristPoint = createEntity();
        touristPoint.setActive(false);
        return touristPoint;
    }

    public static TouristPointResponse createResponse() {
        return new TouristPointResponse(
                UUID.randomUUID(),
                "Praia do Francês",
                "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.",
                Set.of(AccessibilityTypeFactory.createResponse()),
                AddressFactory.createResponse(),
                Set.of(PhotoFactory.createResponse()),
                Set.of(CategoryFactory.createResponse())
        );
    }
}
