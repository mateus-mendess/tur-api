package com.m2.tur.service;

import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.request.TouristPointFilterRequest;
import com.m2.tur.model.dto.response.TouristPointSummaryResponse;
import com.m2.tur.model.entity.Favorite;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.FavoriteRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import com.m2.tur.model.repository.TouristPointSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final TouristPointRepository touristPointRepository;
    private final TouristPointMapper touristPointMapper;
    private final AuthService authService;

    @Cacheable(cacheNames = "tourist-point-favorites", key = "#pageable")
    public Page<TouristPointSummaryResponse> findMyFavorites(TouristPointFilterRequest request, Pageable pageable) {
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
    @CacheEvict(cacheNames = "tourist-point-favorites", allEntries = true)
    @Transactional
    public void addFavorite(UUID touristPointId) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));

        if (favoriteRepository.existsByUserIdAndTouristPointId(user.getId(), touristPointId)) {
            return;
        }

        TouristPoint touristPoint = touristPointRepository.findById(touristPointId)
                        .orElseThrow(() -> new NotFoundException("TouristPoint not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setTouristPoint(touristPoint);

        try {
            favoriteRepository.saveAndFlush(favorite);
        } catch (DataIntegrityViolationException e) {
            //race condition: another request has already inserted the same pair between the exists() call and now — no-op.
        }
    }

    @CacheEvict(cacheNames = "tourist-point-favorites", allEntries = true)
    @Transactional
    public void removeFavorite(UUID touristPointId) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));

        favoriteRepository.deleteByUserIdAndTouristPointId(user.getId(), touristPointId);
    }
}
