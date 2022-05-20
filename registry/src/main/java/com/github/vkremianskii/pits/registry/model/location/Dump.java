package com.github.vkremianskii.pits.registry.model.location;

import com.github.vkremianskii.pits.registry.model.Location;

import static com.github.vkremianskii.pits.registry.model.LocationType.DUMP;

public class Dump extends Location {

    public Dump(int id, String name) {
        super(id, name, DUMP);
    }
}
