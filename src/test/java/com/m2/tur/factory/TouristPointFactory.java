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
                true,
                "Rampas de acesso para cadeirantes disponíveis.",
                AddressFactory.createRequest(),
                Set.of(UUID.randomUUID())
        );
    }

    public static TouristPointRequest createRequestWithoutAccessibilityInfo() {
        return new TouristPointRequest(
                "Praia do Francês",
                "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.",
                true,
                null,
                AddressFactory.createRequest(),
                Set.of(UUID.randomUUID())
        );
    }

    public static TouristPointRequest createRequestWithoutCategories() {
        return new TouristPointRequest(
                "Praia do Francês",
                "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.",
                false,
                null,
                AddressFactory.createRequest(),
                Collections.emptySet()
        );
    }

    public static TouristPointUpdateRequest createUpdateRequest() {
        return new TouristPointUpdateRequest(
                "Praia do Francês",
                "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.",
                true,
                "Rampas de acesso para cadeirantes disponíveis."
        );
    }

    public static TouristPoint createEntity() {
        TouristPoint touristPoint = new TouristPoint();
        touristPoint.setId(UUID.randomUUID());
        touristPoint.setName("Praia do Francês");
        touristPoint.setDescription("Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.");
        touristPoint.setHasAccessibility(true);
        touristPoint.setAccessibilityInfo("Rampas de acesso para cadeirantes disponíveis.");
        touristPoint.setActive(true);
        touristPoint.setCreatedAt(LocalDateTime.now());
        touristPoint.setUser(UserFactory.createEntity());
        touristPoint.setAddress(AddressFactory.createEntity());
        touristPoint.setCategories(Set.of(CategoryFactory.createEntity()));
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
                "Rampas de acesso para cadeirantes disponíveis.",
                AddressFactory.createResponse(),
                Set.of(PhotoFactory.createResponse()),
                Set.of("Praias")
        );
    }
}
