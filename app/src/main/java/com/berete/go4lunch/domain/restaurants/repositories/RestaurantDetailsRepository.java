package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.services.PlaceDetailsProvider;

import javax.inject.Inject;

public class RestaurantDetailsRepository {

  private final PlaceDetailsProvider placeDetailsProvider;

  @Inject
  public RestaurantDetailsRepository(PlaceDetailsProvider placeDetailsProvider){
    this.placeDetailsProvider = placeDetailsProvider;
  }

//  public void getPlaceDetails(String placeId, )

}
