package com.github.vkremianskii.pits.auth;

import com.github.vkremianskii.pits.auth.model.User;
import com.github.vkremianskii.pits.auth.model.UserId;

import java.util.Set;

import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.UserId.userId;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.github.vkremianskii.pits.core.model.Hash.hash;
import static java.util.UUID.randomUUID;

public class TestUser {

    private TestUser() {
    }

    public static User aUser() {
        return new User(
            randomUserId(),
            username("user"),
            hash("user"),
            Set.of(scope("scope")));
    }

    public static UserId randomUserId() {
        return userId(randomUUID());
    }
}
