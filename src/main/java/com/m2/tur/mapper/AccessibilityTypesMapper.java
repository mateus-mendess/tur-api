package com.m2.tur.mapper;

import com.m2.tur.model.dto.response.AccessibilityTypesResponse;
import com.m2.tur.model.entity.AccessibilityTypes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccessibilityTypesMapper {
    AccessibilityTypesResponse toResponse(AccessibilityTypes accessibilityTypes);
}
