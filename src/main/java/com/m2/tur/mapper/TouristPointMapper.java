package com.m2.tur.mapper;

import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.AccessibilityTypes;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.TouristPoint;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TouristPointMapper {
    TouristPoint toEntity(TouristPointRequest request);

    @Mapping(source = "address.state.name", target = "address.state")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.id", target = "userId")
    TouristPointResponse toResponse(TouristPoint touristPoint);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TouristPointUpdateRequest request, @MappingTarget TouristPoint touristPoint);
}
