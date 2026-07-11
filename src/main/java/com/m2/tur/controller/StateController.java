package com.m2.tur.controller;

import com.m2.tur.model.dto.response.StateResponse;
import com.m2.tur.service.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "States", description = "Endpoint for listing all Brazilian states.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/states")
public class StateController {
    private final StateService stateService;

    @Operation(summary = "Lists all states", description = """
            Returns a list of all 27 Brazilian states.
            Used to populate state selection in forms.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "States retrieved successfully.")
    })
    @GetMapping
    public ResponseEntity<List<StateResponse>> getStates() {
        return ResponseEntity.ok(stateService.findAll());
    }
}
