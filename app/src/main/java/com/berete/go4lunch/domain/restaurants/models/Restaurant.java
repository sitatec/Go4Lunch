package com.berete.go4lunch.domain.restaurants.models;

public class Restaurant extends Place {

  public Restaurant(
      String id,
      String name,
      Double stars,
      String photoUrl,
      String address,
      boolean isOpen,
      String[] photoHtmlAttribution,
      GeoCoordinates coordinates) {
    super(id, name, stars, photoUrl, address, isOpen, photoHtmlAttribution, coordinates);
  }

  public Restaurant(Place place) {
    super(
        place.getId(),
        place.getName(),
        place.getStars(),
        place.getPhotoUrl(),
        place.getAddress(),
        place.isOpen(),
        place.getPhotoHtmlAttribution(),
        place.getCoordinates());
  }
}
