package com.berete.go4lunch.domain.restaurants.services;

import androidx.annotation.Nullable;

import com.berete.go4lunch.domain.restaurants.models.Place;

public interface PlaceDetailsProvider {
  void getPlaceDetail(
      String placeId,
      Place.Field[] fieldsToReturn,
      Place.LangCode langCode,
      ResponseListener listener);

  interface ResponseListener {
    void onSuccess(@Nullable Place place);

    void onFailure();
  }
}
