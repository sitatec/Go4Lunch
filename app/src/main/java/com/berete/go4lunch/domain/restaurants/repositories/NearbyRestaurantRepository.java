package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.services.PlaceDataProvider;

import java.util.Arrays;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;

@ParametersAreNonnullByDefault
public class NearbyRestaurantRepository {

  private final PlaceDataProvider placeDataProvider;

  @Inject
  public NearbyRestaurantRepository(PlaceDataProvider placeDataProvider) {
    this.placeDataProvider = placeDataProvider;
  }

  public void getNearbyRestaurants(
      PlaceDataProvider.Callback callback,
      Place.LangCode langCode,
      GeoCoordinates currentLocation) {
    final Place.Type[] types = {Place.Type.RESTAURANT};
    placeDataProvider.setQueryParameters(types, getFields(), currentLocation, langCode);

    placeDataProvider.getPlaceData(new PlaceDataProvider.Callback() {
          @Override public void onSuccess(Place[] places) {
            callback.onSuccess(Arrays.stream(places).map(Restaurant::new).toArray(Restaurant[]::new));
          }

          @Override public void onFailure() {
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
