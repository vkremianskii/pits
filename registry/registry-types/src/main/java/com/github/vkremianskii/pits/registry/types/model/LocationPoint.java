package com.github.vkremianskii.pits.registry.types.model;

import com.github.vkremianskii.pits.core.types.model.LocationId;

public record LocationPoint(int id,
                            LocationId locationId,
                            int order,
                            double latitude,
                            double longitude) {

}
