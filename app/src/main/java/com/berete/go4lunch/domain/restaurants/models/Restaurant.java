package com.berete.go4lunch.domain.restaurants.models;

public class Restaurant extends Place {

  public Restaurant(
      String id,
      String name,
      Double stars,
      String mainPhotoUrl,
      String address,
      boolean isOpen,
      GeoCoordinates coordinates) {
    super(id, name, stars, mainPhotoUrl, address, isOpen,coordinates, null);
  }

  public Restaurant(Place place) {
    super(place);
  }

}
