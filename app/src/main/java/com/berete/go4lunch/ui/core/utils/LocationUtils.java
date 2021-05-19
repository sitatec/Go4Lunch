package com.berete.go4lunch.ui.core.utils;

import android.location.Location;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;

public class LocationUtils {
  /** Return the distance between the 2 params in meter */
  public static Float getDistanceBetween(GeoCoordinates first, GeoCoordinates second) {
    final float[] result = new float[1];
    Location.distanceBetween(
        first.getLatitude(),
        first.getLongitude(),
        second.getLatitude(),
        second.getLongitude(),
        result);
    return result[0];
  }
}
