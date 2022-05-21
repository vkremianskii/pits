package com.github.vkremianskii.pits.registry.types.model.location;

import com.github.vkremianskii.pits.registry.types.model.Location;
import com.github.vkremianskii.pits.registry.types.model.LocationType;

public class Dump extends Location {

    public Dump(int id, String name) {
        super(id, name, LocationType.DUMP);
    }
}
