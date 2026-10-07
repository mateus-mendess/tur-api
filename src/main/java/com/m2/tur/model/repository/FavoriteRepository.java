package com.m2.tur.model.repository;

import com.m2.tur.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID>, JpaSpecificationExecutor<Favorite> {
    @Modifying
    @Query(value = """ 
            INSERT INTO favorites (id, user_id, tourist_point_id)
            VALUES (gen_random_uuid(), :userId, :touristPointId)
            ON CONFLICT (user_id, tourist_point_id) DO NOTHING""",
            nativeQuery = true)
    void insertIgnore(UUID userId, UUID touristPointId);

    void deleteByUserIdAndTouristPointId(UUID userId, UUID touristPointId);
}
