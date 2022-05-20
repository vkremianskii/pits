package com.github.vkremianskii.pits.registry.model.location;

import com.github.vkremianskii.pits.registry.model.Location;

import static com.github.vkremianskii.pits.registry.model.LocationType.STOCKPILE;

public class Stockpile extends Location {

    public Stockpile(int id, String name) {
        super(id, name, STOCKPILE);
    }
}
