package com.github.vkremianskii.pits.registry.types.model;

public record LocationPoint(int id,
                            int locationId,
                            int order,
                            double latitude,
                            double longitude) {

}
