package com.github.vkremianskii.pits.auth.model;

import com.github.vkremianskii.pits.core.Microtype;

import java.util.UUID;

public class UserId extends Microtype<UUID> {

    private UserId(UUID value) {
        super(value);
    }

    public static UserId userId(UUID value) {
        return new UserId(value);
    }

    public static UserId valueOf(String value) {
        return userId(UUID.fromString(value));
    }
}
