package com.github.vkremianskii.pits.registry.types.model;

import java.util.Objects;

public class Position {
    private final double latitude;
    private final double longitude;
    private final int elevation;

    public Position(double latitude, double longitude, int elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public int elevation() {
        return elevation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.latitude, latitude) == 0 && Double.compare(position.longitude, longitude) == 0 && elevation == position.elevation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, elevation);
    }
}
