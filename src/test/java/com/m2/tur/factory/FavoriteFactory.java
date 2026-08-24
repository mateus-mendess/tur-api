package com.m2.tur.factory;

import com.m2.tur.model.entity.Favorite;

import java.time.LocalDateTime;
import java.util.UUID;

public class FavoriteFactory {
    public static Favorite createEntity() {
        Favorite favorite = new Favorite();
        favorite.setId(UUID.randomUUID());
        favorite.setUser(UserFactory.createEntity());
        favorite.setTouristPoint(TouristPointFactory.createEntity());
        favorite.setCreatedAt(LocalDateTime.now());

        return favorite;
    }
}
