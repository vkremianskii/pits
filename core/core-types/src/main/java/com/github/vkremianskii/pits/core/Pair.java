package com.github.vkremianskii.pits.core;

public record Pair<L, R>(L left, R right) {

    public static <L, R> Pair<L, R> pair(L left, R right) {
        return new Pair<>(left, right);
    }
}