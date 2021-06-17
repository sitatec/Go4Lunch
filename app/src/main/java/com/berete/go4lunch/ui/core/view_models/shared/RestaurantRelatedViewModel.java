package com.berete.go4lunch.ui.core.view_models.shared;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.repositories.NearbyRestaurantRepository;
import com.berete.go4lunch.domain.restaurants.services.RestaurantSpecificDataProvider;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.shared.repositories.UserRepository;
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
  static private Restaurant[] lastRequestResult;
  private LocalDateTime lastRequestTime;
  private final RestaurantSpecificDataProvider restaurantSpecificDataProvider;
  private final MutableLiveData<User> currentUser = new MutableLiveData<>();

  @Inject
  public RestaurantRelatedViewModel(
      NearbyRestaurantRepository nearbyRestaurantRepository,
      RestaurantSpecificDataProvider restaurantSpecificDataProvider,
      UserRepository userRepository) {
    this.nearbyRestaurantRepository = nearbyRestaurantRepository;
    this.restaurantSpecificDataProvider = restaurantSpecificDataProvider;
    userRepository.addUserLoginCompleteListener(currentUser::setValue);
  }

  public void getNearbyRestaurants(GeoCoordinates currentLocation, Callback<Place[]> callback) {
    if (cacheUpToDate(currentLocation)) {
      callback.onSuccess(lastRequestResult);
    } else {
      nearbyRestaurantRepository.getNearbyRestaurants(
          transformCallback(callback), Place.LangCode.getSystemLanguage(), currentLocation);
      lastUserLocation = currentLocation;
      lastRequestTime = LocalDateTime.now();
    }
  }

  public void getWorkmatesCountByRestaurant(
      String workplaceId, Callback<Map<String, Integer>> callback) {
    restaurantSpecificDataProvider.getRestaurantClientCountByWorkplace(workplaceId, callback);
  }

  /**
   * This method determines if the view model needs to fetch new data or just return the cached
   * data. To achieve that it checks if 15 minutes are elapsed or the user has moved 50 meters since
   * the last data fetch.
   *
   * @param currentLocation The current location of the device
   * @return true if the cache is up to data, false otherwise
   */
  private boolean cacheUpToDate(GeoCoordinates currentLocation) {
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

  public LiveData<User> getCurrentUser() {
    return currentUser;
  }

  @VisibleForTesting
  public static Restaurant[] getLastRequestResult() {
    return lastRequestResult;
  }
}
