package com.github.vkremianskii.pits.auth.model;

import com.github.vkremianskii.pits.core.Microtype;

public class PasswordHash extends Microtype<String> {

    private PasswordHash(String value) {
        super(value);
    }

    public static PasswordHash passwordHash(String value) {
        return new PasswordHash(value);
    }
}
