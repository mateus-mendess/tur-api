package com.m2.tur.controller;

import com.m2.tur.model.dto.request.CategoryRequest;
import com.m2.tur.model.dto.response.CategoryResponse;
import com.m2.tur.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categories", description = "Endpoints for listing and creating tourist point categories.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Lists all registered categories", description = """
            Returns all active tourist point categories.
            Used to populate category selection in forms.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully.")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @Operation(summary = "Registers category", description = """
            Creates a new tourist point category.
            Requires authentication.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data or category already exists."),
            @ApiResponse(responseCode = "404", description = "Authenticated user not found.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CategoryRequest request) {
        categoryService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
