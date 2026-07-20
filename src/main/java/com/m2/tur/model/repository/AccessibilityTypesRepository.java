package com.m2.tur.model.repository;

import com.m2.tur.model.entity.AccessibilityTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessibilityTypesRepository extends JpaRepository<AccessibilityTypes, Long> {
}
