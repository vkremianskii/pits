package com.github.vkremianskii.pits.registry.types.model;

import com.github.vkremianskii.pits.core.types.model.LocationId;
import com.github.vkremianskii.pits.core.types.model.LocationType;

public record LocationDeclaration(LocationId id,
                                  String name,
                                  LocationType type) {

}
