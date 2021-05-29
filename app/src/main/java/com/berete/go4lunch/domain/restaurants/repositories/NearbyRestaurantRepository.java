package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.services.NearbyPlaceProvider;
import com.berete.go4lunch.domain.utils.Callback;

import java.util.Arrays;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;

@ParametersAreNonnullByDefault
public class NearbyRestaurantRepository {

  private final NearbyPlaceProvider nearbyPlaceProvider;

  @Inject
  public NearbyRestaurantRepository(NearbyPlaceProvider nearbyPlaceProvider) {
    this.nearbyPlaceProvider = nearbyPlaceProvider;
  }

  public void getNearbyRestaurants(
      Callback<Place[]> callback, Place.LangCode langCode, GeoCoordinates currentLocation) {
    final Place.Type[] types = {Place.Type.RESTAURANT};
    nearbyPlaceProvider.setNearbySearchQueryParams(
        types,
        NearbyPlaceProvider.DEFAULT_FIELDS,
        currentLocation,
        langCode,
        NearbyPlaceProvider.DEFAULT_SEARCH_RADIUS);

    nearbyPlaceProvider.getPlaceData(
        new Callback<Place[]>() {
          @Override
          public void onSuccess(Place[] places) {
            callback.onSuccess(
                Arrays.stream(places).map(Restaurant::new).toArray(Restaurant[]::new));
          }

          @Override
          public void onFailure() {
            callback.onFailure();
          }
        });
  }
}
