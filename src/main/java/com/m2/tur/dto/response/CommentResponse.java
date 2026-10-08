package com.m2.tur.dto.response;

public record CommentResponse(
        String content,
        Integer note,
        String authorName
) {}
