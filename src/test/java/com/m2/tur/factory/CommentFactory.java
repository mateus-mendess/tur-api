package com.m2.tur.factory;

import com.m2.tur.model.dto.request.CommentRequest;
import com.m2.tur.model.dto.response.CommentResponse;
import com.m2.tur.model.entity.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentFactory {

    public static CommentRequest createRequest() {
        return new CommentRequest(
                "Lugar incrível, vale muito a visita!",
                5,
                "Mateus Mendes"
        );
    }

    public static CommentRequest createRequestWithInvalidNote() {
        return new CommentRequest(
                "Lugar incrível, vale muito a visita!",
                6,
                "Mateus Mendes"
        );
    }

    public static CommentRequest createRequestWithoutRequiredFields() {
        return new CommentRequest(
                null,
                null,
                null
        );
    }

    public static Comment createEntity() {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setContent("Lugar incrível, vale muito a visita!");
        comment.setNote(5);
        comment.setAuthorName("Mateus Mendes");
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    public static CommentResponse createResponse() {
        return new CommentResponse(
                "Lugar incrível, vale muito a visita!",
                5,
                "Mateus Mendes"
        );
    }
}
