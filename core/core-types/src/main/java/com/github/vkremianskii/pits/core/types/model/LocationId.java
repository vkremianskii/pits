package com.github.vkremianskii.pits.core.types.model;

import com.github.vkremianskii.pits.core.types.Microtype;

import java.util.UUID;

public class LocationId extends Microtype<UUID> {

    private LocationId(UUID value) {
        super(value);
    }

    public static LocationId locationId(UUID value) {
        return new LocationId(value);
    }

    public static LocationId valueOf(String value) {
        return locationId(UUID.fromString(value));
    }
}
