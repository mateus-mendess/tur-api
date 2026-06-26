package com.m2.tur.mapper;

import com.m2.tur.model.dto.request.CategoryRequest;
import com.m2.tur.model.dto.response.CategoryResponse;
import com.m2.tur.model.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);
}
