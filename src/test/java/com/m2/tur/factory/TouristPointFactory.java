package com.m2.tur.factory;

import com.m2.tur.model.dto.request.TouristPointFilterRequest;
import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.response.CommentResponse;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.dto.response.TouristPointSummaryResponse;
import com.m2.tur.model.entity.Comment;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class TouristPointFactory {
    private static final String DEFAULT_NAME = "Praia do Francês";
    private static final String DEFAULT_DESCRIPTION =
            "Uma das praias mais bonitas de Alagoas, com águas cristalinas e areia branca.";

    // ---------- Requests ----------

    public static TouristPointRequest createRequest() {
        return new TouristPointRequest(
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                Set.of(1L),
                AddressFactory.createRequest(),
                Set.of(UUID.randomUUID())
        );
    }

    public static TouristPointRequest createRequestWithoutCategories() {
        return new TouristPointRequest(
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                Set.of(1L),
                AddressFactory.createRequest(),
                Collections.emptySet()
        );
    }

    public static TouristPointRequest createRequestWithoutAccessibilityTypes() {
        return new TouristPointRequest(
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                Collections.emptySet(),
                AddressFactory.createRequest(),
                Set.of(UUID.randomUUID())
        );
    }

    public static TouristPointFilterRequest createFilterRequest() {
        return new TouristPointFilterRequest(
                "Maceió",
                1L,
                1L,
                UUID.randomUUID()
        );
    }

    public static TouristPointFilterRequest createEmptyFilterRequest() {
        return new TouristPointFilterRequest(null, null, null, null);
    }

    // ---------- Entities ----------

    public static TouristPoint createEntity() {
        TouristPoint touristPoint = new TouristPoint();
        touristPoint.setId(UUID.randomUUID());
        touristPoint.setName(DEFAULT_NAME);
        touristPoint.setDescription(DEFAULT_DESCRIPTION);
        touristPoint.setActive(true);
        touristPoint.setCreatedAt(LocalDateTime.now());
        touristPoint.setUser(UserFactory.createEntity());
        touristPoint.setAddress(AddressFactory.createEntity());
        touristPoint.setCategories(Set.of(CategoryFactory.createEntity()));
        touristPoint.setAccessibilityTypes(Set.of(AccessibilityTypeFactory.createEntity()));
        touristPoint.setPhotos(Set.of(PhotoFactory.createEntity(touristPoint)));
        return touristPoint;
    }

    public static TouristPoint createInactiveEntity() {
        TouristPoint touristPoint = createEntity();
        touristPoint.setActive(false);
        return touristPoint;
    }

    // ---------- Responses ----------

    public static TouristPointResponse createResponse() {
        User user = UserFactory.createEntity();
        CommentResponse comment = CommentFactory.createResponse();
        return new TouristPointResponse(
                UUID.randomUUID(),
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                Set.of(CategoryFactory.createResponse()),
                Set.of(AccessibilityTypeFactory.createResponse()),
                AddressFactory.createResponse(),
                Set.of(PhotoFactory.createResponse()),
                5.00,
                user.getName(),
                Set.of(comment),
                user.getId()
        );
    }

    public static TouristPointSummaryResponse createSummaryResponse() {
        return new TouristPointSummaryResponse(
                UUID.randomUUID(),
                DEFAULT_NAME,
                "Maceió",
                "Alagoas",
                Set.of(PhotoFactory.createResponse())
        );
    }
}
