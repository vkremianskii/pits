package com.github.vkremianskii.pits.registry.types.model;

import java.util.UUID;

public record LocationPoint(int id,
                            UUID locationId,
                            int order,
                            double latitude,
                            double longitude) {

}
