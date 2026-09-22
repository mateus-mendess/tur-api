package com.m2.tur.controller;

import com.m2.tur.model.dto.response.StatsResponse;
import com.m2.tur.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "statistics", description = "Endpoints for querying general platform statistics")
@RequiredArgsConstructor
@RestController
@RequestMapping("/stats")
public class StatsController {
    private final StatsService statsService;

    @Operation(summary = "Get community statistics", description = """
            Returns aggregated data about the platform, including the number
            of registered tourist attractions, shared photos, and
            registered contributors (users).
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics successfully returned")
    })
    @GetMapping
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok().body(statsService.getCommunityStats());
    }
}
