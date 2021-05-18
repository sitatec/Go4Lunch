package com.berete.go4lunch.domain.restaurants.services;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;

public interface CurrentLocationProvider {
    public void getCurrentCoordinates(OnCoordinatesResultListener resultListener);

    public interface OnCoordinatesResultListener {
        void onResult(GeoCoordinates geoCoordinates);
    }
}