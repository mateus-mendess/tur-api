package com.m2.tur.service;

import com.m2.tur.core.exception.NotFoundException;
import com.m2.tur.core.exception.UnauthorizedException;
import com.m2.tur.core.validation.PageableGuard;
import com.m2.tur.dto.request.TouristPointFilterRequest;
import com.m2.tur.dto.response.TouristPointSummaryResponse;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.entity.Favorite;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.FavoriteRepository;
import com.m2.tur.model.repository.FavoriteSpecification;
import com.m2.tur.model.repository.TouristPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PageableGuard pageableGuard;

    @Transactional(readOnly = true)
    public Page<TouristPointSummaryResponse> findMyFavorites(TouristPointFilterRequest request, Pageable pageable) {
        pageableGuard.pageableValidate(pageable);

        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));

        Specification<Favorite> spec = FavoriteSpecification.byUser(user.getId())
                .and(FavoriteSpecification.byCity(request.city()))
                .and(FavoriteSpecification.byState(request.stateId()))
                .and(FavoriteSpecification.byCategory(request.categoryId()))
                .and(FavoriteSpecification.byAccessibility(request.accessibilityId()));

        return favoriteRepository.findAll(spec, pageable)
                .map(Favorite::getTouristPoint)
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
