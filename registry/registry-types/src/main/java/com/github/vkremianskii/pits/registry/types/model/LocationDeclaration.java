package com.github.vkremianskii.pits.registry.types.model;

import com.github.vkremianskii.pits.core.types.model.LocationId;

public record LocationDeclaration(LocationId id,
                                  String name,
                                  LocationType type) {

}
