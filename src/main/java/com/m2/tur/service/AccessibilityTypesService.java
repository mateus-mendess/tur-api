package com.m2.tur.service;

import com.m2.tur.infra.exception.ForbiddenException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.AccessibilityTypesMapper;
import com.m2.tur.model.dto.request.AccessibilityUpdateRequest;
import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.model.entity.AccessibilityTypes;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.AccessibilityTypesRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AccessibilityTypesService {
    private final AccessibilityTypesRepository accessibilityTypesRepository;
    private final AccessibilityTypesMapper accessibilityTypesMapper;
    private final TouristPointRepository  touristPointRepository;
    private final AuthService authService;

    @Cacheable(cacheNames = "accessibility-types")
    public List<AccessibilityTypesResponse> findAllAccessibilityTypes() {
        return accessibilityTypesRepository.findAll()
                .stream()
                .map(accessibilityTypesMapper::toResponse)
                .toList();
    }
}
