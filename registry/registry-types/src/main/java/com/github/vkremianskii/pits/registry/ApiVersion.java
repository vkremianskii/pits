package com.github.vkremianskii.pits.registry;

import java.util.Objects;

public class ApiVersion {

    public static final ApiVersion EQUIPMENT_RESPONSE_OBJECT = apiVersion(2);

    private final int value;

    private ApiVersion(int value) {
        this.value = value;
    }

    public boolean isGreaterThanOrEqual(ApiVersion other) {
        return value >= other.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiVersion that = (ApiVersion) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public static ApiVersion apiVersion(int value) {
        return new ApiVersion(value);
    }
}
