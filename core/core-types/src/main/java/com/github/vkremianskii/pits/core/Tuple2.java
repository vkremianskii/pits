package com.github.vkremianskii.pits.core;

public record Tuple2<L, R>(L first, R second) {

    public static <L, R> Tuple2<L, R> tuple(L first, R second) {
        return new Tuple2<>(first, second);
    }
}