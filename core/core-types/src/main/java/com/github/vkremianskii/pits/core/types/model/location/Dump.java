package com.github.vkremianskii.pits.core.types.model.location;

import com.github.vkremianskii.pits.core.types.model.LatLngPoint;
import com.github.vkremianskii.pits.core.types.model.Location;
import com.github.vkremianskii.pits.core.types.model.LocationId;

import java.util.List;

import static com.github.vkremianskii.pits.core.types.model.LocationType.DUMP;

public class Dump extends Location {

    public Dump(LocationId id,
                String name,
                List<LatLngPoint> geometry) {
        super(id, name, DUMP, geometry);
    }
}
