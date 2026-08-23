package com.m2.tur.service;

import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.Favorite;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.FavoriteRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final TouristPointRepository touristPointRepository;
    private final TouristPointMapper touristPointMapper;
    private final AuthService authService;

    public List<TouristPointResponse> findMyFavorites() {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));

        return favoriteRepository.findByUserId(user.getId())
                .stream()
                .map(Favorite::getTouristPoint)
                .map(touristPointMapper::toResponse)
                .toList();
    }

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

    @Transactional
    public void removeFavorite(UUID touristPointId) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));

        favoriteRepository.deleteByUserIdAndTouristPointId(user.getId(), touristPointId);
    }
}
