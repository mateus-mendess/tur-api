package com.m2.tur.controller;

import com.m2.tur.dto.request.TouristPointFilterRequest;
import com.m2.tur.dto.response.TouristPointSummaryResponse;
import com.m2.tur.service.TouristPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/me")
public class MeController {
    private final TouristPointService touristPointService;

    @Operation(summary = "List the authenticated user's tourist points", description = """
            Returns all tourist points registered by the currently authenticated user.
            The user is resolved from the JWT bearer token; no user identifier is
            accepted as a request parameter, preventing access to other users' data.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tourist points retrieved successfully. Returns an empty list if the user has not registered any."),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/tourist-points")
    public ResponseEntity<List<TouristPointSummaryResponse>> listMyTouristPoints(TouristPointFilterRequest request,
                                                                                 @ParameterObject
                                                                                 @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                 Pageable pageable) {
        return ResponseEntity.ok(touristPointService.findMyTouristPoints(request, pageable).getContent());
    }
}
