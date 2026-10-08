package com.m2.tur.model.repository;

import com.m2.tur.model.entity.AccessibilityTypes;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.Favorite;
import com.m2.tur.model.entity.TouristPoint;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class FavoriteSpecification {
    public static Specification<Favorite> byUser(UUID userId) {
        return (root, query, builder) ->
                builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Favorite> byCity(String city) {
        return (root, query, builder) -> {
            if (city == null || city.isBlank()) return builder.conjunction();
            return builder.like(
                    builder.lower(root.get("touristPoint").get("address").get("city")),
                    "%" + city.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<Favorite> byState(Long stateId) {
        return (root, query, builder) -> {
            if (stateId == null) return builder.conjunction();
            return builder.equal(
                    root.get("touristPoint").get("address").get("state").get("id"),
                    stateId
            );
        };
    }

    public static Specification<Favorite> byCategory(UUID categoryId) {
        return (root, query, builder) -> {
            if (categoryId == null) return builder.conjunction();
            query.distinct(true);
            Join<Favorite, TouristPoint> touristPoint = root.join("touristPoint");
            Join<TouristPoint, Category> categories = touristPoint.join("categories");
            return builder.equal(categories.get("id"), categoryId);
        };
    }

    public static Specification<Favorite> byAccessibility(Long accessibilityId) {
        return (root, query, builder) -> {
            if (accessibilityId == null) return builder.conjunction();
            query.distinct(true);
            Join<Favorite, TouristPoint> touristPoint = root.join("touristPoint");
            Join<TouristPoint, AccessibilityTypes> accessibilityTypes = touristPoint.join("accessibilityTypes");
            return builder.equal(accessibilityTypes.get("id"), accessibilityId);
        };
    }
}
