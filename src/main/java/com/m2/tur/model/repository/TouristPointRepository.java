package com.m2.tur.model.repository;

import com.m2.tur.model.entity.TouristPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TouristPointRepository extends JpaRepository<TouristPoint, UUID>, JpaSpecificationExecutor<TouristPoint> {}
