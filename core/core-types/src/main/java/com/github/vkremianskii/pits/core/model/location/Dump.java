package com.github.vkremianskii.pits.core.model.location;

import com.github.vkremianskii.pits.core.model.LocationId;
import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.core.model.Location;

import java.util.List;

import static com.github.vkremianskii.pits.core.model.LocationType.DUMP;

public class Dump extends Location {

    public Dump(LocationId id,
                String name,
                List<LatLngPoint> geometry) {
        super(id, name, DUMP, geometry);
    }
}
