package com.m2.tur.mapper;

import com.m2.tur.model.dto.response.StateResponse;
import com.m2.tur.model.entity.State;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StateMapper {
    StateResponse toResponse(State state);
}
