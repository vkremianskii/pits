package com.github.vkremianskii.pits.auth.model;

import com.github.vkremianskii.pits.core.Microtype;

public class Username extends Microtype<String> {

    private Username(String value) {
        super(value);
    }

    public static Username username(String value) {
        return new Username(value);
    }
}
