package com.m2.tur.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CommentRequest(
        @Size(max = 300)
        @NotBlank(message = "content required.")
        String content,

        @NotNull(message = "note required.")
        @Min(1)
        @Max(5)
        Integer note,

        @Schema(description = "author name. Only letters and spaces allowed.", example = "Mateus Mendes")
        @Size(min = 2, max = 100)
        @NotBlank(message = "name required.")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]$",
                message = "invalid name.")
        String authorName
) {}
