package com.github.vkremianskii.pits.registry.app.model.location;

import com.github.vkremianskii.pits.registry.app.model.Location;

import static com.github.vkremianskii.pits.registry.app.model.LocationType.HOLE;

public class Hole extends Location {

    public Hole(int id, String name) {
        super(id, name, HOLE);
    }
}
