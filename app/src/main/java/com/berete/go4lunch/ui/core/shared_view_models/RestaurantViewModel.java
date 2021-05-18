package com.berete.go4lunch.ui.core.shared_view_models;

import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.NearbyRestaurantRepository;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.services.PlaceDataProvider;
import com.berete.go4lunch.domain.restaurants.services.RestaurantsAutocomplete;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantViewModel extends ViewModel {

  private final NearbyRestaurantRepository nearbyRestaurantRepository;
  private final RestaurantsAutocomplete restaurantsAutocomplete;
  private int test = 0;

  @Inject
  public RestaurantViewModel(
      NearbyRestaurantRepository nearbyRestaurantRepository,
      RestaurantsAutocomplete restaurantsAutocomplete) {
    this.nearbyRestaurantRepository = nearbyRestaurantRepository;
    this.restaurantsAutocomplete = restaurantsAutocomplete;
  }

  public void getNearbyRestaurants(
      GeoCoordinates currentLocation, PlaceDataProvider.Callback callback) {
    nearbyRestaurantRepository.getNearbyRestaurants(callback, Place.LangCode.fr, currentLocation);
  }
}
