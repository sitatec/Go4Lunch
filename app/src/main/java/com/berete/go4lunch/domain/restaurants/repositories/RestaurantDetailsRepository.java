package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.services.PlaceDetailsProvider;
import com.berete.go4lunch.domain.utils.Callback;

import javax.inject.Inject;

public class RestaurantDetailsRepository {

  private final PlaceDetailsProvider placeDetailsProvider;

  @Inject
  public RestaurantDetailsRepository(PlaceDetailsProvider placeDetailsProvider){
    this.placeDetailsProvider = placeDetailsProvider;
  }

  public void getRestaurantDetails(
      String restaurantId,
      Place.Field[] fieldsToReturn,
      Place.LangCode langCode,
      Callback<Place> listener){
    placeDetailsProvider.getPlaceDetail(restaurantId, fieldsToReturn, langCode, listener);
  }

}
