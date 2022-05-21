package com.github.vkremianskii.pits.registry.types.model.location;

import com.github.vkremianskii.pits.registry.types.model.Location;
import com.github.vkremianskii.pits.registry.types.model.LocationType;

public class Stockpile extends Location {

    public Stockpile(int id, String name) {
        super(id, name, LocationType.STOCKPILE);
    }
}
