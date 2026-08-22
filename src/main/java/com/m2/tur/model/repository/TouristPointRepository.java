package com.m2.tur.model.repository;

import com.m2.tur.model.entity.TouristPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TouristPointRepository extends JpaRepository<TouristPoint, UUID> {
    List<TouristPoint> findByUserId(UUID userId);
}
