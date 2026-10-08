package com.m2.tur.mapper;

import com.m2.tur.dto.request.AddressRequest;
import com.m2.tur.model.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressRequest request, Double latitude, Double longitude, @MappingTarget Address address);
}
