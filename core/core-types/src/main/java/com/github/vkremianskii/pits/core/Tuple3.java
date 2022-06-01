package com.github.vkremianskii.pits.core;

public record Tuple3<T1, T2, T3>(T1 first, T2 second, T3 third) {

    public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple3(T1 first, T2 second, T3 third) {
        return new Tuple3<>(first, second, third);
    }
}
