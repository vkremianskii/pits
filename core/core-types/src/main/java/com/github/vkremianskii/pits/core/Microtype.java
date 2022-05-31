package com.github.vkremianskii.pits.core;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Microtype<T extends Comparable<T>> implements Comparable<Microtype<T>> {

    public final T value;

    public Microtype(T value) {
        this.value = requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Microtype<?> microtype = (Microtype<?>) o;
        return Objects.equals(value, microtype.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(@NotNull Microtype<T> o) {
        return value.compareTo(o.value);
    }
}
