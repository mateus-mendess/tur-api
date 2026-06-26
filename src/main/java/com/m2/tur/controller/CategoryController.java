package com.m2.tur.controller;

import com.m2.tur.model.dto.request.CategoryRequest;
import com.m2.tur.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CategoryRequest request) {
        categoryService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
