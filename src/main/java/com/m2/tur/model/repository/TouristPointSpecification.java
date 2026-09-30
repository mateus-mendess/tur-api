package com.m2.tur.model.repository;

import com.m2.tur.model.entity.AccessibilityTypes;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.Favorite;
import com.m2.tur.model.entity.TouristPoint;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TouristPointSpecification {

    public static Specification<TouristPoint> byCity(String city) {
        return (root, query, builder) -> {
            if (city == null || city.isBlank()) return builder.conjunction();

            return builder.like(builder.lower(root.get("address").get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    public static Specification<TouristPoint> byState(UUID stateId) {
        return (root, query, builder) -> {
            if (stateId == null) return builder.conjunction();

            return builder.equal(root.get("address").get("state").get("id"), stateId);
        };
    }

    public static Specification<TouristPoint> byCategory(UUID categoryId) {
        return (root, query, builder) -> {
            if (categoryId == null) return builder.conjunction();

            query.distinct(true);
            Join<TouristPoint, Category> join = root.join("categories");
            return builder.equal(join.get("id"), categoryId);
        };
    }

    public static Specification<TouristPoint> byAccessibility(UUID accessibilityId) {
        return (root, query, builder) -> {
            if (accessibilityId == null) return builder.conjunction();

            query.distinct(true);
            Join<TouristPoint, AccessibilityTypes> join = root.join("accessibilityTypes");
            return builder.equal(join.get("id"), accessibilityId);
        };
    }

    public static Specification<TouristPoint> belongsToUser(UUID userId) {
        return (root, query, builder) -> builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<TouristPoint> favoritesByUser(UUID userId) {
        return (root, query, builder) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Favorite> favoriteRoot = subquery.from(Favorite.class);

            subquery.select(favoriteRoot.get("id"))
                    .where(
                            builder.equal(favoriteRoot.get("user").get("id"), userId),
                            builder.equal(favoriteRoot.get("touristPoint").get("id"), root.get("id"))
                    );

            return builder.exists(subquery);
        };
    }
}
