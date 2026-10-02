package com.m2.tur.factory;

import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.dto.response.UserResponse;
import com.m2.tur.model.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserFactory {

    private static final String DEFAULT_NAME = "João Silva";
    private static final String DEFAULT_EMAIL = "joao.silva@email.com";
    private static final String DEFAULT_PASSWORD = "Secret@123";

    // ---------- Requests ----------

    public static UserRequest createRequest() {
        return new UserRequest(
                DEFAULT_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD
        );
    }

    public static UserRequest createRequestWithInvalidEmail() {
        return new UserRequest(
                DEFAULT_NAME,
                "invalid-email",
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD
        );
    }

    public static UserRequest createRequestWithInvalidPassword() {
        return new UserRequest(
                DEFAULT_NAME,
                DEFAULT_EMAIL,
                "weakpassword",
                "weakpassword"
        );
    }

    // ---------- Entities ----------

    public static User createEntity() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(DEFAULT_NAME);
        user.setEmail(DEFAULT_EMAIL);
        user.setPassword(DEFAULT_PASSWORD);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static User createInactiveEntity() {
        User user = createEntity();
        user.setActive(false);
        return user;
    }

    // ---------- Responses ----------

    public static UserResponse createResponse() {
        return new UserResponse(
                UUID.randomUUID(),
                DEFAULT_NAME,
                DEFAULT_EMAIL,
                LocalDateTime.now()
        );
    }
}
