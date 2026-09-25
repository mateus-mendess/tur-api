package com.m2.tur.mapper;

import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.entity.AccessibilityTypes;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.TouristPoint;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PhotoMapper.class})
public interface TouristPointMapper {
    TouristPoint toEntity(TouristPointRequest request);

    @Mapping(source = "address.state.name", target = "address.state")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.id", target = "userId")
    TouristPointResponse toResponse(TouristPoint touristPoint);

    @Mapping(source = "touristPoint.address.state.name", target = "address.state")
    @Mapping(source = "touristPoint.user.name", target = "userName")
    @Mapping(source = "touristPoint.user.id", target = "userId")
    @Mapping(source = "averageRating", target = "averageRating")
    TouristPointResponse toResponse(TouristPoint touristPoint, Double averageRating);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TouristPointUpdateRequest request, @MappingTarget TouristPoint touristPoint);
}
