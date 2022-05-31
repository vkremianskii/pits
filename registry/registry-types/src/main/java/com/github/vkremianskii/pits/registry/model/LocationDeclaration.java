package com.github.vkremianskii.pits.registry.model;

import com.github.vkremianskii.pits.core.model.LocationId;
import com.github.vkremianskii.pits.core.model.LocationType;

public record LocationDeclaration(LocationId id,
                                  String name,
                                  LocationType type) {

}
