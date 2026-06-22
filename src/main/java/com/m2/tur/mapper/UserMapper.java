package com.m2.tur.mapper;

import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest request);
}
