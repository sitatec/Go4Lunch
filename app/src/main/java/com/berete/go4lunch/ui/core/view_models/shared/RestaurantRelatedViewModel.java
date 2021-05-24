package com.berete.go4lunch.ui.core.view_models.shared;

import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.repositories.NearbyRestaurantRepository;
import com.berete.go4lunch.domain.restaurants.services.RestaurantSpecificDataProvider;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.domain.utils.DistanceUtils;

import java.time.LocalDateTime;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantRelatedViewModel extends ViewModel {

  private final NearbyRestaurantRepository nearbyRestaurantRepository;
  private GeoCoordinates lastUserLocation;
  private Restaurant[] lastRequestResult;
  private LocalDateTime lastRequestTime;
  private final RestaurantSpecificDataProvider restaurantSpecificDataProvider;


  @Inject
  public RestaurantRelatedViewModel(NearbyRestaurantRepository nearbyRestaurantRepository, RestaurantSpecificDataProvider restaurantSpecificDataProvider) {
    this.nearbyRestaurantRepository = nearbyRestaurantRepository;
    this.restaurantSpecificDataProvider = restaurantSpecificDataProvider;
  }

  public void getNearbyRestaurants(
      GeoCoordinates currentLocation, Callback<Place[]> callback) {
    if (cacheUpToDate(currentLocation)) {
      callback.onSuccess(lastRequestResult);
    } else {
      nearbyRestaurantRepository.getNearbyRestaurants(
          transformCallback(callback), Place.LangCode.fr, currentLocation);
      lastUserLocation = currentLocation;
      lastRequestTime = LocalDateTime.now();
    }
  }

  public void getWorkmatesCountByRestaurant(String workplaceId, Callback<Map<String, Integer>> callback) {
    restaurantSpecificDataProvider.getRestaurantClientCountByWorkplace(workplaceId, callback);
  }

  public boolean cacheUpToDate(GeoCoordinates currentLocation) {
    return lastUserLocation != null
        && DistanceUtils.getDistanceBetween(lastUserLocation, currentLocation) < 50 // meter
        && lastRequestTime.plusMinutes(15).isAfter(LocalDateTime.now());
    // ^ 15 minutes are not elapsed since the last request. ^
  }

  private Callback<Place[]> transformCallback(Callback<Place[]> callback) {
    return new Callback<Place[]>() {
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
