package com.berete.go4lunch.domain.utils;

import android.annotation.SuppressLint;
import android.location.Location;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;

public class DistanceUtils {
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

  public static String getDisplayableDistance(GeoCoordinates first, GeoCoordinates second){
    return convertToDisplayableDistance(getDistanceBetween(first,second));
  }

  @SuppressLint("DefaultLocale")
  public static String convertToDisplayableDistance(Float distanceInMeter){
    if(distanceInMeter < 100) return Math.round(distanceInMeter) + " m";
    if(distanceInMeter < 1000) return Math.round(distanceInMeter) + "m";
    if((distanceInMeter % 1000) < 100 ) return Math.round(distanceInMeter / 1000) + " km";
    return String.format("%.1fkm", distanceInMeter / 1000);
  }

}
