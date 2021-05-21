package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;
import com.berete.go4lunch.domain.utils.Callback;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

@ViewModelScoped
public class RestaurantNamePredictionsRepository {

  private final AutocompleteService autocompleteService;
  private Callback<Prediction[]> autocompleteResultListener;

  @Inject
  public RestaurantNamePredictionsRepository(AutocompleteService autocompleteService) {
    this.autocompleteService = autocompleteService;
  }

  public void predict(String input, GeoCoordinates currentLocation, Place.LangCode langCode) {
    assert autocompleteResultListener != null;
    autocompleteService.predict(
        input,
        currentLocation,
        langCode,
        AutocompleteService.DEFAULT_COVERED_RADIUS,
        autocompleteResultListener);
  }

  public void subscribeForResults(Callback<Prediction[]> listener) {
    this.autocompleteResultListener = listener;
  }
}
