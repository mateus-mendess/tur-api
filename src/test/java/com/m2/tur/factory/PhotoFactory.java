package com.m2.tur.factory;

import com.m2.tur.model.dto.response.PhotoResponse;
import com.m2.tur.model.entity.Photo;

import java.time.LocalDateTime;
import java.util.UUID;

public class PhotoFactory {
    public static Photo createEntity() {
        Photo photo = new Photo();
        photo.setId(UUID.randomUUID());
        photo.setPath("550e8400-e29b-41d4-a716-446655440000");
        photo.setCreatedAt(LocalDateTime.now());
        return photo;
    }

    public static PhotoResponse createResponse() {
        return new PhotoResponse(
                UUID.randomUUID(),
                "550e8400-e29b-41d4-a716-446655440000"
        );
    }
}
