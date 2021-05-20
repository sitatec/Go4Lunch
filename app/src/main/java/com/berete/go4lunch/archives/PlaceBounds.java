package com.berete.go4lunch.archives;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;

public class PlaceBounds {
    private double north;
    private double est;
    private double west;
    private double south;

    public PlaceBounds(GeoCoordinates northEst, GeoCoordinates southWest){
        this(
            northEst.getLatitude(), northEst.getLongitude(),
            southWest.getLatitude(), southWest.getLongitude()
        );
    }

    public PlaceBounds(double north, double est, double south, double west) {
        this.north = north;
        this.est = est;
        this.west = west;
        this.south = south;
    }

    public double getNorth() {
        return north;
    }

    public double getEst() {
        return est;
    }

    public double getWest() {
        return west;
    }

    public double getSouth() {
        return south;
    }
}
