package com.m2.tur.service;

import com.m2.tur.infra.exception.BusinessException;
import com.m2.tur.infra.exception.ForbiddenException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.dto.response.TouristPointSummaryResponse;
import com.m2.tur.model.entity.*;
import com.m2.tur.model.repository.AccessibilityTypesRepository;
import com.m2.tur.model.repository.CategoryRepository;
import com.m2.tur.model.repository.CommentRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TouristPointService {
    private final TouristPointRepository touristPointRepository;
    private final TouristPointMapper touristPointMapper;
    private final AuthService authService;
    private final AddressService addressService;
    private final PhotoService photoService;
    private final CategoryRepository categoryRepository;
    private final AccessibilityTypesRepository accessibilityTypesRepository;
    private final CommentRepository commentRepository;

    @Cacheable(cacheNames = "tourist-point-summary", key = "#pageable")
    public Page<TouristPointSummaryResponse> findAll(Pageable pageable) {
        return touristPointRepository.findAll(pageable)
                .map(touristPointMapper::toSummaryResponse);
    }

    @Cacheable(cacheNames = "tourist-point", key = "#id")
    public TouristPointResponse findById(UUID id) {
        TouristPoint touristPoint = touristPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TouristPoint not found"));

        Double averageRating = commentRepository.findAverageRatingByTouristPointId(id);

        return touristPointMapper.toResponse(touristPoint, averageRating);
    }

    @Cacheable(cacheNames = "tourist-point-summary", key = "#pageable")
    public Page<TouristPointSummaryResponse> findMyTouristPoints(Pageable pageable) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in."));

        return touristPointRepository.findByUserId(user.getId(), pageable)
                .map(touristPointMapper::toSummaryResponse);
    }

    @Transactional
    @CacheEvict(cacheNames = "tourist-point-summary", allEntries = true)
    public TouristPointResponse save(TouristPointRequest request) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in"));

        Address address = addressService.create(request.addressRequest());

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoriesIds()));

        Set<AccessibilityTypes> accessibilityTypes = new HashSet<>(
                accessibilityTypesRepository.findAllById(request.accessibilityTypesIds())
        );

        validate(request, categories, accessibilityTypes);

        TouristPoint touristPoint = touristPointMapper.toEntity(request);
        touristPoint.associate(user, address, categories, accessibilityTypes);

        touristPointRepository.save(touristPoint);

        Double averageRating = commentRepository.findAverageRatingByTouristPointId(touristPoint.getId());

        return touristPointMapper.toResponse(touristPoint, averageRating);
    }

    @Transactional
    @CacheEvict(cacheNames = "tourist-point", key = "#id")
    public void update(UUID id, TouristPointRequest request) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in"));

        TouristPoint touristPoint = touristPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tourist Point not found"));

        if (!touristPoint.getUser().equals(user)) {
            throw new ForbiddenException("You don't have permission to update this tourist point");
        }

        Address address = addressService.create(request.addressRequest());

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoriesIds()));

        Set<AccessibilityTypes> accessibilityTypes = new HashSet<>(
                accessibilityTypesRepository.findAllById(request.accessibilityTypesIds())
        );

        validate(request, categories, accessibilityTypes);

        touristPointMapper.updateEntity(request, address, categories, accessibilityTypes, touristPoint);
    }

    @Transactional
    @CacheEvict(cacheNames = "tourist-point", key = "#id")
    public void delete(UUID id) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in."));

        TouristPoint touristPoint = touristPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tourist Point not found"));

        if (!touristPoint.getUser().equals(user)) {
            throw new ForbiddenException("You don't have permission to update this tourist point");
        }

        for (Photo photo : touristPoint.getPhotos()) {
            photoService.delete(photo.getId());
        }

        touristPointRepository.delete(touristPoint);
    }

    private void validate(TouristPointRequest request, Set<Category> categories, Set<AccessibilityTypes> accessibilityTypes) {
        if (categories.size() != request.categoriesIds().size()) {
            throw new NotFoundException("Category not found.");
        }

        if (accessibilityTypes.size() != request.accessibilityTypesIds().size()) {
            throw new NotFoundException("Accessibility not found.");
        }
    }
}
