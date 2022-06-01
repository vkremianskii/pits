package com.github.vkremianskii.pits.registry.dto;

import com.github.vkremianskii.pits.registry.model.Location;

import java.util.List;

public record LocationsResponse(List<Location> locations) {

}
