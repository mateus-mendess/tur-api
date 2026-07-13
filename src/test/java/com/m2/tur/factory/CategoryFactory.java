package com.m2.tur.factory;

import com.m2.tur.model.dto.request.CategoryRequest;
import com.m2.tur.model.dto.response.CategoryResponse;
import com.m2.tur.model.entity.Category;

import java.time.LocalDateTime;
import java.util.UUID;

public class CategoryFactory {
    public static CategoryRequest createRequest() {
        return new CategoryRequest(
                "Beaches"
        );
    }

    public static Category createEntity() {
        Category category = new Category();
        category.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        category.setName("Beaches");
        category.setActive(true);
        category.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        category.setUser(UserFactory.createEntity());

        return category;
    }

    public static CategoryResponse createResponse() {
        return new CategoryResponse(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Beaches"
        );
    }
}
