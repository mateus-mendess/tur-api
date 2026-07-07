package com.m2.tur.mapper;

import com.m2.tur.model.entity.Photo;
import com.m2.tur.model.entity.TouristPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
    Photo toEntity(String path);
}
