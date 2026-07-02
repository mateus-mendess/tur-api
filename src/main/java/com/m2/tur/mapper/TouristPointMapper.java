package com.m2.tur.mapper;

import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.TouristPoint;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TouristPointMapper {
    TouristPoint toEntity(TouristPointRequest request);

    @Mapping(source = "address", target = "addressResponse")
    @Mapping(source = "address.state.name", target = "addressResponse.state")
    @Mapping(source = "categories", target = "categories")
    TouristPointResponse toResponse(TouristPoint touristPoint);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TouristPointUpdateRequest request, @MappingTarget TouristPoint touristPoint);

    default String mapCategory(Category category) {
        return category.getName();
    }
}
