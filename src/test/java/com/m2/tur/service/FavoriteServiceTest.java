package com.m2.tur.service;

import com.m2.tur.factory.FavoriteFactory;
import com.m2.tur.factory.TouristPointFactory;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.Favorite;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.FavoriteRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @Nested
    class FindMyFavorites {
        @Test
        void should_return_tourist_points_favorites_with_success() {
            //Arrange
            TouristPointResponse response = TouristPointFactory.createResponse();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(favoriteRepository.findByUserId(any(UUID.class))).thenReturn(List.of(FavoriteFactory.createEntity()));
            when(touristPointMapper.toResponse(any(TouristPoint.class))).thenReturn(response);

            //Act & Assert
            var result = assertDoesNotThrow(() -> favoriteService.findMyFavorites());

            verify(authService).getAuthenticatedUser();
            verify(favoriteRepository).findByUserId(any(UUID.class));
            verify(touristPointMapper).toResponse(any(TouristPoint.class));

            assertEquals(response.userId(), result.get(0).userId());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, ()-> favoriteService.findMyFavorites());

            verify(authService).getAuthenticatedUser();
            verify(favoriteRepository, times(0)).findByUserId(any(UUID.class));
            verify(touristPointMapper, times(0)).toResponse(any(TouristPoint.class));
        }
    }

    @Nested
    class AddFavorite {
        @Test
        void should_favorite_tourist_point_with_success() {
            //Arrange
            Favorite favorite = FavoriteFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(favoriteRepository.existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class))).thenReturn(false);
            when(touristPointRepository.findById(any(UUID.class))).thenReturn(Optional.of(favorite.getTouristPoint()));
            when(favoriteRepository.saveAndFlush(any(Favorite.class))).thenReturn(favorite);

            //Act & Assert
            assertDoesNotThrow(() -> favoriteService.addFavorite(favorite.getTouristPoint().getId()));

            verify(authService).getAuthenticatedUser();
            verify(favoriteRepository).existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class));
            verify(touristPointRepository).findById(any(UUID.class));
            verify(favoriteRepository).saveAndFlush(favoriteCaptor.capture());

            var captured = favoriteCaptor.getValue();

            assertEquals(favorite.getTouristPoint().getId(), captured.getTouristPoint().getId());
            assertNotNull(captured.getUser());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, ()-> favoriteService.addFavorite(UUID.randomUUID()));

            verify(authService).getAuthenticatedUser();
            verify(touristPointRepository, times(0)).findById(any(UUID.class));
            verify(favoriteRepository, times(0)).saveAndFlush(any(Favorite.class));
        }

        @Test
        void should_return_without_error_when_tourist_point_is_already_favorite() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(UserFactory.createEntity()));
            when(favoriteRepository.existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class))).thenReturn(true);

            //Act & Assert
            assertDoesNotThrow(() -> favoriteService.addFavorite(UUID.randomUUID()));

            verify(authService).getAuthenticatedUser();
            verify(favoriteRepository).existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class));
            verify(touristPointRepository, times(0)).findById(any(UUID.class));
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

            verify(authService).getAuthenticatedUser();
            verify(favoriteRepository).existsByUserIdAndTouristPointId(any(UUID.class), any(UUID.class));
            verify(touristPointRepository).findById(any(UUID.class));
            verify(favoriteRepository, times(0)).saveAndFlush(any(Favorite.class));
        }
    }

    @Nested
    class RemoveFavorite {
        @Test
        void should_remove_favorite_tourist_point_with_success() {
            //Arrange
            Favorite favorite = FavoriteFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(favorite.getUser()));
            doNothing().when(favoriteRepository).deleteByUserIdAndTouristPointId(favorite.getUser().getId(), favorite.getTouristPoint().getId());

            //Act & Assert
            assertDoesNotThrow(() -> favoriteService.removeFavorite(favorite.getTouristPoint().getId()));

            verify(authService).getAuthenticatedUser();
            verify(favoriteRepository).deleteByUserIdAndTouristPointId(favorite.getUser().getId(), favorite.getTouristPoint().getId());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> favoriteService.removeFavorite(UUID.randomUUID()));

            verify(authService).getAuthenticatedUser();
            verify(favoriteRepository, times(0)).deleteByUserIdAndTouristPointId(any(UUID.class), any(UUID.class));
        }
    }
}
