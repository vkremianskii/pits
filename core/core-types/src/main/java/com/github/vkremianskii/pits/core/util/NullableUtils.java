package com.github.vkremianskii.pits.core.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class NullableUtils {

    private NullableUtils() {
    }

    public static <O, R> R mapNotNull(@Nullable O obj, Function<O, R> mapper) {
        if (obj == null) {
            return null;
        }
        return mapper.apply(obj);
    }

    public static <O, R> R mapNotNull(@Nullable O obj, R defValue, Function<O, R> mapper) {
        if (obj == null) {
            return defValue;
        }
        return mapper.apply(obj);
    }
}
