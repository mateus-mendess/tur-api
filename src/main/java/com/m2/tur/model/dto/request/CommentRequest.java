package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.*;

public record CommentRequest(
        @NotBlank(message = "content required.")
        String content,

        @NotNull(message = "note required.")
        @Min(1)
        @Max(5)
        Integer note,

        @NotBlank(message = "name required.")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]{2,100}$",
                message = "invalid name.")
        String authorName
) {}
