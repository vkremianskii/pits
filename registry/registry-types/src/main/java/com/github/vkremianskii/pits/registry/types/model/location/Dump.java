package com.github.vkremianskii.pits.registry.types.model.location;

import com.github.vkremianskii.pits.registry.types.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.types.model.Location;

import java.util.List;
import java.util.UUID;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.DUMP;

public class Dump extends Location {

    public Dump(UUID id,
                String name,
                List<LatLngPoint> geometry) {
        super(id, name, DUMP, geometry);
    }
}
