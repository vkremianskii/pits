package com.github.vkremianskii.pits.registry;

import com.github.vkremianskii.pits.registry.model.LocationId;
import com.github.vkremianskii.pits.registry.model.location.Dump;
import com.github.vkremianskii.pits.registry.model.location.Face;
import com.github.vkremianskii.pits.registry.model.location.Hole;
import com.github.vkremianskii.pits.registry.model.location.Stockpile;

import static com.github.vkremianskii.pits.registry.model.LocationId.locationId;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

public class TestLocations {

    public static Dump aDump() {
        return new Dump(randomLocationId(), "Some dump", emptyList());
    }

    public static Face aFace() {
        return new Face(randomLocationId(), "Some face", emptyList());
    }

    public static Hole aHole() {
        return new Hole(randomLocationId(), "Some hole", emptyList());
    }

    public static Stockpile aStockpile() {
        return new Stockpile(randomLocationId(), "Some stockpile", emptyList());
    }

    public static LocationId randomLocationId() {
        return locationId(randomUUID());
    }

    private TestLocations() {
    }
}
