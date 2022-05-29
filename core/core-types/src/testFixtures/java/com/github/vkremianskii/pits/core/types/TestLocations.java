package com.github.vkremianskii.pits.core.types;

import com.github.vkremianskii.pits.core.types.model.LocationId;
import com.github.vkremianskii.pits.core.types.model.location.Dump;
import com.github.vkremianskii.pits.core.types.model.location.Face;
import com.github.vkremianskii.pits.core.types.model.location.Hole;
import com.github.vkremianskii.pits.core.types.model.location.Stockpile;

import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.LocationId.locationId;
import static java.util.Collections.emptyList;

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
        return locationId(UUID.randomUUID());
    }

    private TestLocations() {
    }
}
