package com.m2.tur.model.dto.response;

public record CommentResponse(
        String content,
        Integer note,
        String authorName
) {}
