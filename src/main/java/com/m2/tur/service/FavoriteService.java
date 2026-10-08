package com.m2.tur.service;

import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.dto.request.TouristPointFilterRequest;
import com.m2.tur.dto.response.TouristPointSummaryResponse;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.FavoriteRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import com.m2.tur.model.repository.TouristPointSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final TouristPointRepository touristPointRepository;
    private final TouristPointMapper touristPointMapper;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<TouristPointSummaryResponse> findMyFavorites(TouristPointFilterRequest request, Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!order.getProperty().equals("createdAt")) {
                throw new IllegalArgumentException("Dynamic sorting blocked.");
            }
        }

        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));

        Specification<TouristPoint> spec = TouristPointSpecification.favoritesByUser(user.getId())
                .and(TouristPointSpecification.byCity(request.city()))
                .and(TouristPointSpecification.byState(request.stateId()))
                .and(TouristPointSpecification.byCategory(request.categoryId()))
                .and(TouristPointSpecification.byAccessibility(request.accessibilityId()));

        return touristPointRepository.findAll(spec, pageable)
                .map(touristPointMapper::toSummaryResponse);
    }

    @Transactional
    public void addFavorite(UUID touristPointId) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));

        if (!touristPointRepository.existsById(touristPointId)) {
            throw new NotFoundException("Tourist Point Not Found");
        }

        favoriteRepository.insertIgnore(user.getId(), touristPointId);
    }

    @Transactional
    public void removeFavorite(UUID touristPointId) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));

        favoriteRepository.deleteByUserIdAndTouristPointId(user.getId(), touristPointId);
    }
}
