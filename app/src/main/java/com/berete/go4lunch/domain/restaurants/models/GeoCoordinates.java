package com.berete.go4lunch.domain.restaurants.models;

public class GeoCoordinates {
    private double latitude;
    private double longitude;

    public GeoCoordinates() {}

    public GeoCoordinates(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
