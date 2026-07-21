package com.m2.tur.service;

import com.m2.tur.infra.exception.BusinessException;
import com.m2.tur.infra.exception.ForbiddenException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.*;
import com.m2.tur.model.repository.AccessibilityTypesRepository;
import com.m2.tur.model.repository.CategoryRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public List<TouristPointResponse> findAll() {
        return touristPointRepository.findAll()
                .stream()
                .map(touristPointMapper::toResponse)
                .toList();
    }

    public TouristPointResponse findById(UUID id) {
        return touristPointMapper.toResponse(
                touristPointRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Tourist Point not found"))
        );
    }

    @Transactional
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

        return touristPointMapper.toResponse(touristPointRepository.save(touristPoint));
    }

    @Transactional
    public void update(TouristPointUpdateRequest request, UUID id) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in"));

        TouristPoint touristPoint = touristPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tourist Point not found"));

        if (!touristPoint.getUser().equals(user)) {
            throw new ForbiddenException("You don't have permission to update this tourist point");
        }

        touristPointMapper.updateEntity(request, touristPoint);

        touristPointRepository.save(touristPoint);
    }

    @Transactional
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
