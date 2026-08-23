package com.m2.tur.model.repository;

import com.m2.tur.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    boolean existsByUserIdAndTouristPointId(UUID userId, UUID touristPointId);
    void deleteByUserIdAndTouristPointId(UUID userId, UUID touristPointId);
    List<Favorite> findByUserId(UUID id);
}
