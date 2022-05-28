package com.github.vkremianskii.pits.registry.types.dto;

import com.github.vkremianskii.pits.registry.types.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.types.model.LocationType;

import java.util.List;

public record CreateLocationRequest(String name,
                                    LocationType type,
                                    List<LatLngPoint> geometry) {

}
