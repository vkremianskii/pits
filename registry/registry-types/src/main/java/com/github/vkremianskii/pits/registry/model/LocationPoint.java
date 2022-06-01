package com.github.vkremianskii.pits.registry.model;

public record LocationPoint(int id,
                            LocationId locationId,
                            int order,
                            double latitude,
                            double longitude) {

}
