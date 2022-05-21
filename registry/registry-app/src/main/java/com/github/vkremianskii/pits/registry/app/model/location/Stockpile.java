package com.github.vkremianskii.pits.registry.app.model.location;

import com.github.vkremianskii.pits.registry.app.model.Location;
import com.github.vkremianskii.pits.registry.app.model.LocationType;

public class Stockpile extends Location {

    public Stockpile(int id, String name) {
        super(id, name, LocationType.STOCKPILE);
    }
}
