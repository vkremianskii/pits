package com.github.vkremianskii.pits.registry.types.model;

import java.util.UUID;

public record LocationDeclaration(UUID id,
                                  String name,
                                  LocationType type) {

}
