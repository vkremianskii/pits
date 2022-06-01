package com.github.vkremianskii.pits.registry.dto;

import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.model.LocationType;

import java.util.List;

public record CreateLocationRequest(String name,
                                    LocationType type,
                                    List<LatLngPoint> geometry) {

}
