package com.m2.tur.controller;

import com.m2.tur.model.dto.request.TouristPointFilterRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.dto.response.TouristPointSummaryResponse;
import com.m2.tur.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/me/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(summary = "List the authenticated user's favorite tourist points", description = """
            Returns all tourist points the authenticated user has marked as favorite.
            The user is resolved from the JWT bearer token; no user identifier is
            accepted as a request parameter, preventing access to other users' data.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorite tourist points successfully retrieved. Returns an empty list if the user has no favorites."),
            @ApiResponse(responseCode = "401", description = "User not authenticated.")
    })
    @GetMapping
    public ResponseEntity<List<TouristPointSummaryResponse>> listMyFavorites(TouristPointFilterRequest request,
                                                                             @ParameterObject
                                                                             @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                             Pageable pageable) {
        return ResponseEntity.ok(favoriteService.findMyFavorites(request, pageable).getContent());
    }
    @Operation(summary = "Add a tourist point to the authenticated user's favorites", description = """
            Marks the given tourist point as a favorite for the authenticated user.
            This operation is idempotent: if the tourist point is already favorited,
            the request succeeds without creating a duplicate entry.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tourist point successfully added to favorites."),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Tourist point not found")
    })
    @PutMapping("/{touristPointId}")
    public ResponseEntity<Void> addFavorite(@PathVariable UUID touristPointId) {
        favoriteService.addFavorite(touristPointId);

        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Remove a tourist point from the authenticated user's favorites", description = """
            Unmarks the given tourist point as a favorite for the authenticated user.
            This operation is idempotent: if the tourist point is not currently
            favorited, the request still succeeds and no error is returned.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tourist point removed from favorites"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @DeleteMapping("/{touristPointId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable UUID touristPointId) {
        favoriteService.removeFavorite(touristPointId);

        return ResponseEntity.noContent().build();
    }
}
