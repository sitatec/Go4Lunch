package com.berete.go4lunch.ui.core.view_models.shared;

import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.repositories.NearbyRestaurantRepository;
import com.berete.go4lunch.domain.restaurants.services.NearbyPlaceProvider;
import com.berete.go4lunch.domain.utils.DistanceUtils;

import java.time.LocalDateTime;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantViewModel extends ViewModel {

  private final NearbyRestaurantRepository nearbyRestaurantRepository;
  private GeoCoordinates lastUserLocation;
  private Restaurant[] lastRequestResult;
  private LocalDateTime lastRequestTime;

  @Inject
  public RestaurantViewModel(NearbyRestaurantRepository nearbyRestaurantRepository) {
    this.nearbyRestaurantRepository = nearbyRestaurantRepository;
  }

  public void getNearbyRestaurants(
      GeoCoordinates currentLocation, NearbyPlaceProvider.Callback callback) {
    if (cacheUpToDate(currentLocation)) {
      callback.onSuccess(lastRequestResult);
    } else {
      nearbyRestaurantRepository.getNearbyRestaurants(
          transformCallback(callback), Place.LangCode.fr, currentLocation);
      lastUserLocation = currentLocation;
      lastRequestTime = LocalDateTime.now();
    }
  }

  public boolean cacheUpToDate(GeoCoordinates currentLocation) {
    return lastUserLocation != null
        && DistanceUtils.getDistanceBetween(lastUserLocation, currentLocation) > 50
        && lastRequestTime.plusMinutes(15).isAfter(LocalDateTime.now());
    // ^ 15 minutes are not elapsed since the last request. ^
  }

  private NearbyPlaceProvider.Callback transformCallback(NearbyPlaceProvider.Callback callback) {
    return new NearbyPlaceProvider.Callback() {
      @Override
      public void onSuccess(Place[] places) {
        lastRequestResult = (Restaurant[]) places;
        callback.onSuccess(places);
      }

      @Override
      public void onFailure() {
        callback.onFailure();
      }
    };
  }
}