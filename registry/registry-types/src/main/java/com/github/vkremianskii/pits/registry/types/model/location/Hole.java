package com.github.vkremianskii.pits.registry.types.model.location;

import com.github.vkremianskii.pits.core.types.model.LocationId;
import com.github.vkremianskii.pits.registry.types.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.types.model.Location;

import java.util.List;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.HOLE;

public class Hole extends Location {

    public Hole(LocationId id,
                String name,
                List<LatLngPoint> geometry) {
        super(id, name, HOLE, geometry);
    }
}
