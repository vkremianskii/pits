package com.github.vkremianskii.pits.registry.types.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class Location {

    public final UUID id;
    public final String name;
    public final LocationType type;
    public final List<LatLngPoint> geometry;

    protected Location(UUID id,
                       String name,
                       LocationType type,
                       List<LatLngPoint> geometry) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.type = requireNonNull(type);
        this.geometry = requireNonNull(geometry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return id.equals(location.id) && Objects.equals(name, location.name) && type == location.type && Objects.equals(geometry, location.geometry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, geometry);
    }

    @Override
    public String toString() {
        return "Location{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", geometry=" + geometry +
            '}';
    }
}
