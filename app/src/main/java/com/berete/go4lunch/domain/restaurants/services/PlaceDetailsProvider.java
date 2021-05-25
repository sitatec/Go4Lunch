package com.berete.go4lunch.domain.restaurants.services;

import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.utils.Callback;

public interface PlaceDetailsProvider {
  void getPlaceDetail(
      String placeId,
      Place.Field[] fieldsToReturn,
      Place.LangCode langCode,
      Callback<Place> listener);

}
