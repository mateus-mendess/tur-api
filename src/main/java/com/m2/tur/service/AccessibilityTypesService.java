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
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AccessibilityTypesService {
    private final AccessibilityTypesRepository accessibilityTypesRepository;
    private final AccessibilityTypesMapper accessibilityTypesMapper;
    private final TouristPointRepository  touristPointRepository;
    private final AuthService authService;

    public List<AccessibilityTypesResponse> findAllAccessibilityTypes() {
        return accessibilityTypesRepository.findAll()
                .stream()
                .map(accessibilityTypesMapper::toResponse)
                .toList();
    }

    @Transactional
    public void update(UUID touristPointId, AccessibilityUpdateRequest request) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated."));

        TouristPoint touristPoint  = touristPointRepository.findById(touristPointId)
                .orElseThrow(() -> new NotFoundException("Tourist Point not found."));

        if (!touristPoint.getUser().equals(user)) {
            throw new ForbiddenException("User not authorized.");
        }

        Set<AccessibilityTypes> accessibilityTypes = new HashSet<>(
                accessibilityTypesRepository.findAllById(request.accessibilityTypesIds())
        );

        if (accessibilityTypes.size() != request.accessibilityTypesIds().size()) {
            throw new NotFoundException("Accessibility Types not found.");
        }

        touristPoint.setAccessibilityTypes(accessibilityTypes);
    }
}
