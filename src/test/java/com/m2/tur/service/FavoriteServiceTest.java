package com.m2.tur.service;

import com.m2.tur.factory.FavoriteFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.request.TouristPointFilterRequest;
import com.m2.tur.model.dto.response.TouristPointSummaryResponse;
import com.m2.tur.model.entity.Favorite;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.repository.FavoriteRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {
    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private TouristPointRepository touristPointRepository;

    @Mock
    private TouristPointMapper touristPointMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private FavoriteService favoriteService;

    @Captor
    private ArgumentCaptor<Favorite> favoriteCaptor;

    private Favorite favorite;
    private TouristPointFilterRequest filterRequest;
    private TouristPoint touristPoint;
    private TouristPointSummaryResponse touristPointSummaryResponse;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        favorite = FavoriteFactory.createEntity();
        filterRequest = TouristPointFactory.createFilterRequest();
        touristPoint = favorite.getTouristPoint();
        touristPointSummaryResponse = TouristPointFactory.createSummaryResponse();
        pageable = PageRequest.of(0, 10);
    }

    @Nested
    class FindMyFavorites {
        @Test
        void should_return_tourist_points_favorites_with_success() {
            //Arrange
            Page<TouristPoint> page = new PageImpl<>(List.of(touristPoint), pageable, 10);

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(touristPointRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(touristPointMapper.toSummaryResponse(any(TouristPoint.class))).thenReturn(touristPointSummaryResponse);

            //Act & Assert
            var result = favoriteService.findMyFavorites(filterRequest, pageable);

            verify(authService).getAuthenticatedUser();

            assertNotNull(result.getContent());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, ()-> favoriteService.findMyFavorites(filterRequest, pageable));

            verify(favoriteRepository, times(0)).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Nested
    class AddFavorite {
        @Test
        void should_favorite_tourist_point_with_success() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(favoriteRepository.existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class))).thenReturn(false);
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(touristPoint));

            //Act & Assert
            favoriteService.addFavorite(touristPoint.getId());

            verify(favoriteRepository).saveAndFlush(favoriteCaptor.capture());

            var captured = favoriteCaptor.getValue();

            assertNotNull(captured.getUser());
            assertNotNull(captured.getTouristPoint());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, ()-> favoriteService.addFavorite(touristPoint.getId()));

            verify(favoriteRepository, times(0)).saveAndFlush(any(Favorite.class));
        }

        @Test
        void should_return_without_error_when_tourist_point_is_already_favorite() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(favoriteRepository.existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class))).thenReturn(true);

            //Act & Assert
            favoriteService.addFavorite(touristPoint.getId());

            verify(favoriteRepository).existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class));
            verify(favoriteRepository, times(0)).saveAndFlush(any(Favorite.class));
        }

        @Test
        void should_throw_not_found_exception_when_tourist_point_not_found() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(favoriteRepository.existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class))).thenReturn(false);
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> favoriteService.addFavorite(UUID.randomUUID()));

            verify(favoriteRepository, times(0)).saveAndFlush(any(Favorite.class));
        }
    }

    @Nested
    class RemoveFavorite {
        @Test
        void should_remove_favorite_tourist_point_with_success() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(favorite.getUser()));

            //Act & Assert
            favoriteService.removeFavorite(favorite.getTouristPoint().getId());

            verify(favoriteRepository).deleteByUserIdAndTouristPointId(favorite.getUser().getId(), favorite.getTouristPoint().getId());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> favoriteService.removeFavorite(touristPoint.getId()));

            verify(favoriteRepository, times(0)).deleteByUserIdAndTouristPointId(any(UUID.class), any(UUID.class));
        }
    }
}
