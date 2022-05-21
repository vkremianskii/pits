package com.github.vkremianskii.pits.registry.app.model;

public class Position {
    private final double latitude;
    private final double longitude;
    private final int elevation;

    public Position(double latitude, double longitude, int elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public int elevation() {
        return elevation;
    }
}
