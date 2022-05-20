package com.github.vkremianskii.pits.registry.model;

public class Position {
    private final double latitude;
    private final double longitude;
    private final int elevation;

    public Position(double latitude, double longitude, int elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getElevation() {
        return elevation;
    }
}
