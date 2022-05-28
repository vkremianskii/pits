package com.github.vkremianskii.pits.registry.types.dto;

import com.github.vkremianskii.pits.registry.types.model.LocationType;

import java.util.List;

public record LocationsResponse(List<Location> locations) {

    public record Location(int id,
                           String name,
                           LocationType type,
                           List<LocationPoint> points) {

    }

    public record LocationPoint(double latitude, double longitude) {

    }
}
