package com.m2.tur.controller;

import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.service.AccessibilityTypesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accessibilities")
public class AccessibilityTypesController {
    private final AccessibilityTypesService accessibilityTypesService;

    @GetMapping
    public ResponseEntity<List<AccessibilityTypesResponse>> getAllAccessibilityTypes() {
        return ResponseEntity.ok(accessibilityTypesService.findAllAccessibilityTypes());
    }
}

