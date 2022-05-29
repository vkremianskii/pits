package com.github.vkremianskii.pits.core.types.model;

public record LocationPoint(int id,
                            LocationId locationId,
                            int order,
                            double latitude,
                            double longitude) {

}
