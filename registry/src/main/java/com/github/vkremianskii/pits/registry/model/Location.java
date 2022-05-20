package com.github.vkremianskii.pits.registry.model;

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
}
