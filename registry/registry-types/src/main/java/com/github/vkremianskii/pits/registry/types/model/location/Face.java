package com.github.vkremianskii.pits.registry.types.model.location;

import com.github.vkremianskii.pits.registry.types.model.Location;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.FACE;

public class Face extends Location {

    public Face(int id, String name) {
        super(id, name, FACE);
    }
}
