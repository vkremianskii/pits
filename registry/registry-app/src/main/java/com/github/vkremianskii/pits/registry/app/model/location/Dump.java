package com.github.vkremianskii.pits.registry.app.model.location;

import com.github.vkremianskii.pits.registry.app.model.Location;
import com.github.vkremianskii.pits.registry.app.model.LocationType;

public class Dump extends Location {

    public Dump(int id, String name) {
        super(id, name, LocationType.DUMP);
    }
}
