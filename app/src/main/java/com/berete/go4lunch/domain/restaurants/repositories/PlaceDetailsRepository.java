package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.services.PlaceDetailsProvider;
import com.berete.go4lunch.domain.utils.Callback;

import javax.inject.Inject;

public class PlaceDetailsRepository {

  private final PlaceDetailsProvider placeDetailsProvider;

  @Inject
  public PlaceDetailsRepository(PlaceDetailsProvider placeDetailsProvider){
    this.placeDetailsProvider = placeDetailsProvider;
  }

  public void getPlaceDetails(
      String restaurantId,
      Place.Field[] fieldsToReturn,
      Place.LangCode langCode,
      Callback<Place> listener){
    placeDetailsProvider.getPlaceDetail(restaurantId, fieldsToReturn, langCode, listener);
  }

}
