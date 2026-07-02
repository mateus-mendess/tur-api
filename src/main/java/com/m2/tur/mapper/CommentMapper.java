package com.m2.tur.mapper;

import com.m2.tur.model.dto.response.CommentResponse;
import com.m2.tur.model.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentResponse toResponse(Comment comment);
}
