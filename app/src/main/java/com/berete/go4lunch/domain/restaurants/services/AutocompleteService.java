package com.berete.go4lunch.domain.restaurants.services;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.domain.utils.Callback;

public interface AutocompleteService {

  public static final int DEFAULT_COVERED_RADIUS = 9000;

  public void predict(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Integer radiusInMeter,
      Callback<Prediction[]> listener);

  public void predict(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Callback<Prediction[]> listener);

  public void predictWithFilter(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Integer radiusInMeter,
      Place.Type[] filter,
      Callback<Prediction[]> listener
  );

  public void predictWithFilter(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Place.Type[] filter,
      Callback<Prediction[]> listener
  );

}
