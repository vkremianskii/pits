package com.github.vkremianskii.pits.registry.types.model.location;

import com.github.vkremianskii.pits.registry.types.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.types.model.Location;

import java.util.List;
import java.util.UUID;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.HOLE;

public class Hole extends Location {

    public Hole(UUID id,
                String name,
                List<LatLngPoint> geometry) {
        super(id, name, HOLE, geometry);
    }
}
