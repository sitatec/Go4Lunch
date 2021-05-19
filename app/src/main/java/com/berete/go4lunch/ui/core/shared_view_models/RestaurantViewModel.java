package com.berete.go4lunch.ui.core.shared_view_models;

import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.repositories.NearbyRestaurantRepository;
import com.berete.go4lunch.domain.restaurants.services.PlaceDataProvider;
import com.berete.go4lunch.domain.restaurants.services.RestaurantsAutocomplete;
import com.berete.go4lunch.ui.core.utils.LocationUtils;

import java.time.LocalDateTime;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantViewModel extends ViewModel {

  private final NearbyRestaurantRepository nearbyRestaurantRepository;
  private final RestaurantsAutocomplete restaurantsAutocomplete;
  private GeoCoordinates lastUserLocation;
  private Restaurant[] lastRequestResult;
  private LocalDateTime lastRequestTime;

  @Inject
  public RestaurantViewModel(
      NearbyRestaurantRepository nearbyRestaurantRepository,
      RestaurantsAutocomplete restaurantsAutocomplete) {
    this.nearbyRestaurantRepository = nearbyRestaurantRepository;
    this.restaurantsAutocomplete = restaurantsAutocomplete;
  }

  public void getNearbyRestaurants(
      GeoCoordinates currentLocation, PlaceDataProvider.Callback callback) {
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
        && LocationUtils.getDistanceBetween(lastUserLocation, currentLocation) > 50
        && lastRequestTime.plusMinutes(15).isAfter(LocalDateTime.now());
        // ^ 15 minutes are not elapsed since the last request. ^
  }

  private PlaceDataProvider.Callback transformCallback(PlaceDataProvider.Callback callback) {
    return new PlaceDataProvider.Callback() {
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
