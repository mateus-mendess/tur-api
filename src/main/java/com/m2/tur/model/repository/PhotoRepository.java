package com.m2.tur.model.repository;

import com.m2.tur.model.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    int countByTouristPointId(UUID touristPointId);
}
