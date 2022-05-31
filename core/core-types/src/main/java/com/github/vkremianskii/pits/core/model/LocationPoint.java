package com.github.vkremianskii.pits.core.model;

public record LocationPoint(int id,
                            LocationId locationId,
                            int order,
                            double latitude,
                            double longitude) {

}
