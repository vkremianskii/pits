package com.github.vkremianskii.pits.registry.model.location;

import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.model.Location;
import com.github.vkremianskii.pits.registry.model.LocationId;

import java.util.List;

import static com.github.vkremianskii.pits.registry.model.LocationType.DUMP;

public class Dump extends Location {

    public Dump(LocationId id,
                String name,
                List<LatLngPoint> geometry) {
        super(id, name, DUMP, geometry);
    }
}
