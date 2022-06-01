package com.github.vkremianskii.pits.registry.model.location;

import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.model.Location;
import com.github.vkremianskii.pits.registry.model.LocationId;

import java.util.List;

import static com.github.vkremianskii.pits.registry.model.LocationType.HOLE;

public class Hole extends Location {

    public Hole(LocationId id,
                String name,
                List<LatLngPoint> geometry) {
        super(id, name, HOLE, geometry);
    }
}
