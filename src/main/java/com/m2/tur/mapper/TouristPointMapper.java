package com.m2.tur.mapper;

import com.m2.tur.dto.request.TouristPointRequest;
import com.m2.tur.dto.response.TouristPointResponse;
import com.m2.tur.dto.response.TouristPointSummaryResponse;
import com.m2.tur.model.entity.AccessibilityTypes;
import com.m2.tur.model.entity.Address;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.TouristPoint;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {PhotoMapper.class})
public interface TouristPointMapper {
    TouristPoint toEntity(TouristPointRequest request);

    @Mapping(source = "touristPoint.address.state.name", target = "address.state")
    @Mapping(source = "touristPoint.user.name", target = "userName")
    @Mapping(source = "touristPoint.user.id", target = "userId")
    @Mapping(source = "averageRating", target = "averageRating")
    TouristPointResponse toResponse(TouristPoint touristPoint, Double averageRating);

    TouristPointSummaryResponse toSummaryResponse(TouristPoint touristPoint);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "address", target = "address")
    @Mapping(source = "categories", target = "categories")
    @Mapping(source = "accessibilityTypes", target = "accessibilityTypes")
    void updateEntity(TouristPointRequest request, Address address, Set<Category> categories, Set<AccessibilityTypes> accessibilityTypes, @MappingTarget TouristPoint touristPoint);
}
