package com.github.vkremianskii.pits.core.model;

import com.github.vkremianskii.pits.core.Microtype;

public class Hash extends Microtype<String> {

    private Hash(String value) {
        super(value);
    }

    public static Hash hash(String value) {
        return new Hash(value);
    }
}
