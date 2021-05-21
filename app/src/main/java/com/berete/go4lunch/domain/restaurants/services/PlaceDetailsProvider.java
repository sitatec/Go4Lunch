package com.berete.go4lunch.domain.restaurants.services;

import com.berete.go4lunch.domain.restaurants.models.Place;

public interface PlaceDetailsProvider {
  void getPlaceDetail(
      String placeId,
      Place.Field[] fieldsToReturn,
      Place.LangCode langCode,
      ResponseListener listener);

  interface ResponseListener {
    void onSuccess(Place place);

    void onFailure();
  }
}
