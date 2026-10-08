package com.m2.tur.mapper;

import com.m2.tur.dto.request.CommentRequest;
import com.m2.tur.dto.response.CommentResponse;
import com.m2.tur.model.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentRequest commentRequest);

    CommentResponse toResponse(Comment comment);
}
