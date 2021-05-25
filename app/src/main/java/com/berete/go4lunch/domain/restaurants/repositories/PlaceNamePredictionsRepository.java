package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;
import com.berete.go4lunch.domain.utils.Callback;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

@ViewModelScoped
public class PlaceNamePredictionsRepository {

  private final AutocompleteService autocompleteService;
  private Callback<Prediction[]> autocompleteResultListener;
  private Callback<Prediction[]> filteredAutocompleteResultListener;

  @Inject
  public PlaceNamePredictionsRepository(AutocompleteService autocompleteService) {
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

  public void predictWithFilter(
      String input, GeoCoordinates currentLocation, Place.LangCode langCode, Place.Type[] filter) {
    assert filteredAutocompleteResultListener != null;
    autocompleteService.predictWithFilter(
        input,
        currentLocation,
        langCode,
        AutocompleteService.DEFAULT_COVERED_RADIUS,
        filter,
        filteredAutocompleteResultListener);
  }

  public void subscribeForResults(Callback<Prediction[]> listener) {
    this.autocompleteResultListener = listener;
  }

  public void subscribeForFilteredResult(Callback<Prediction[]> listener) {
    this.filteredAutocompleteResultListener = listener;
  }
}
