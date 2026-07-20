package com.m2.tur.service;

import com.m2.tur.mapper.AccessibilityTypesMapper;
import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.model.repository.AccessibilityTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccessibilityTypesService {
    private final AccessibilityTypesRepository accessibilityTypesRepository;
    private final AccessibilityTypesMapper accessibilityTypesMapper;

    public List<AccessibilityTypesResponse> findAllAccessibilityTypes() {
        return accessibilityTypesRepository.findAll()
                .stream()
                .map(accessibilityTypesMapper::toResponse)
                .toList();
    }
}
