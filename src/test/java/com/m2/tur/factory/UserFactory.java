package com.m2.tur.factory;

import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserFactory {

    public static UserRequest createRequest() {
        return new UserRequest(
                "João Silva",
                "joao.silva@email.com",
                "Secret@123",
                "Secret@123"
        );
    }

    public static UserRequest createRequestWithInvalidEmail() {
        return new UserRequest(
                "João Silva",
                "invalid-email",
                "Secret@123",
                "Secret@123"
        );
    }

    public static UserRequest createRequestWithInvalidPassword() {
        return new UserRequest(
                "João Silva",
                "joao.silva@email.com",
                "weakpassword",
                "weakpassword"
        );
    }

    public static User createEntity() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("João Silva");
        user.setEmail("joao.silva@email.com");
        user.setPassword("Secret@123");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static User createInactiveEntity() {
        User user = createEntity();
        user.setActive(false);
        return user;
    }
}
