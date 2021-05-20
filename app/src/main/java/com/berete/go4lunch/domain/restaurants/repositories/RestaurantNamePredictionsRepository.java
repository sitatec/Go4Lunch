package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

@ViewModelScoped
public class RestaurantNamePredictionsRepository {

  private final AutocompleteService autocompleteService;
  private AutocompleteService.ResultListener autocompleteResultListener;

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

  public void subscribeForResults(AutocompleteService.ResultListener listener) {
    this.autocompleteResultListener = listener;
  }
}
