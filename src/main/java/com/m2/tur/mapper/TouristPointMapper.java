package com.m2.tur.mapper;

import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.TouristPoint;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TouristPointMapper {
    TouristPoint toEntity(TouristPointRequest request);

    TouristPointResponse toResponse(TouristPoint touristPoint);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TouristPointRequest request, @MappingTarget TouristPoint touristPoint);
}
