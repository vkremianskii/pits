package com.github.vkremianskii.pits.core.model;

import com.github.vkremianskii.pits.core.Microtype;

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
