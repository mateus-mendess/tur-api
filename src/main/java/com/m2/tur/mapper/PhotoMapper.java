package com.m2.tur.mapper;

import com.m2.tur.model.dto.response.PhotoResponse;
import com.m2.tur.model.entity.Photo;
import com.m2.tur.model.entity.TouristPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {StorageUrlMapper.class})
public interface PhotoMapper {
    Photo toEntity(String path);

    @Mapping(source = "path", target = "url", qualifiedByName = "fullUrl")
    PhotoResponse toResponse(Photo photo);
}
