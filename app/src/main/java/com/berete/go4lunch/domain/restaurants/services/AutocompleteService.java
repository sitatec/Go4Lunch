package com.berete.go4lunch.domain.restaurants.services;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Prediction;

public interface AutocompleteService {

  public static final int DEFAULT_COVERED_RADIUS = 9000;

  public void predict(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Integer radiusInMeter,
      ResultListener listener);

  public void predict(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      ResultListener listener);

  public interface ResultListener {
    void onSuccess(Prediction[] predictions);
    void onFailure();
  }
}
