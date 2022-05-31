package com.github.vkremianskii.pits.auth.model;

import com.github.vkremianskii.pits.core.Microtype;

public class Scope extends Microtype<String> {

    private Scope(String value) {
        super(value);
    }

    public static Scope scope(String value) {
        return new Scope(value);
    }
}
