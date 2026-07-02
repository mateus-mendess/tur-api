package com.m2.tur.service;

import com.m2.tur.infra.exception.BusinessException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.Address;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.CategoryRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final CategoryRepository categoryRepository;

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
    public TouristPointResponse save(TouristPointRequest request) throws IOException, InterruptedException {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in"));

        Address address = addressService.buildAddress(request.addressRequest());

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoriesIds()));

        TouristPoint touristPoint = touristPointMapper.toEntity(request);
        touristPoint.associate(user, address, categories);

        return touristPointMapper.toResponse(touristPointRepository.save(touristPoint));
    }

    @Transactional
    public void update(TouristPointUpdateRequest request, UUID id) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in"));

        TouristPoint touristPoint = touristPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tourist Point not found"));

        if (!touristPoint.getUser().equals(user)) {
            throw new UnauthorizedException("User not logged in");
        }

        touristPointMapper.updateEntity(request, touristPoint);

        validateUpdate(touristPoint);

        touristPointRepository.save(touristPoint);
    }

    @Transactional
    public void delete(UUID id) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in."));

        TouristPoint touristPoint = touristPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tourist Point not found"));

        if (!touristPoint.getUser().equals(user)) {
            throw new UnauthorizedException("User not logged in");
        }

        touristPointRepository.delete(touristPoint);
    }

    private void validateUpdate(TouristPoint touristPoint) {
        if (Boolean.TRUE.equals(touristPoint.getHasAccessibility())) {

            if (touristPoint.getAccessibilityInfo() == null || touristPoint.getAccessibilityInfo().isBlank()) {
                throw new BusinessException("accessibilityInfo must be provided when hasAccessibility is true.");
            }

        } else if (Boolean.FALSE.equals(touristPoint.getHasAccessibility())) {
            touristPoint.setAccessibilityInfo(null);
        }
    }

}
