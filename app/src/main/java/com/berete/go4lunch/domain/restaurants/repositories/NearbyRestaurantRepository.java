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
      Callback<Place[]> callback,
      Place.LangCode langCode,
      GeoCoordinates currentLocation) {
    final Place.Type[] types = {Place.Type.RESTAURANT};
    nearbyPlaceProvider.setQueryParameters(
        types, getFields(), currentLocation, langCode, NearbyPlaceProvider.DEFAULT_SEARCH_RADIUS);
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

  private Place.Field[] getFields() {
    return new Place.Field[] {
      Place.Field.ADDRESS, Place.Field.GEO_COORDINATES,
      Place.Field.NAME, Place.Field.OPENING_HOURS,
      Place.Field.PHONE_NUMBER, Place.Field.PHOTO_URL,
      Place.Field.WEBSITE_URL, Place.Field.RATE
    };
  }
}
