package com.m2.tur.mapper;

import com.m2.tur.dto.response.PhotoResponse;
import com.m2.tur.model.entity.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StorageUrlMapper.class})
public interface PhotoMapper {
    Photo toEntity(String path);

    @Mapping(source = "path", target = "url", qualifiedByName = "fullUrl")
    PhotoResponse toResponse(Photo photo);
}
