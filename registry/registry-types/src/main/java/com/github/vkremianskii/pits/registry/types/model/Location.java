package com.github.vkremianskii.pits.registry.types.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Location {
    private final int id;
    private final String name;
    private final LocationType type;

    protected Location(int id, String name, LocationType type) {
        this.id = id;
        this.name = requireNonNull(name);
        this.type = requireNonNull(type);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return id == location.id && Objects.equals(name, location.name) && type == location.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
}
