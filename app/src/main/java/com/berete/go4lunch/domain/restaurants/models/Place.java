package com.berete.go4lunch.domain.restaurants.models;

import java.util.Locale;

public class Place {

  private final String id;
  private final String name;
  private final double stars;
  private final String photoUrl;
  private final String address;
  private final boolean isOpen;
  private final GeoCoordinates coordinates;
  private final String[] photoHtmlAttribution;

  public Place(
      String id,
      String name,
      Double stars,
      String photoUrl,
      String address,
      boolean isOpen,
      String[] photoHtmlAttribution,
      GeoCoordinates coordinates) {
    this.id = id;
    this.name = name;
    this.stars = stars;
    this.photoUrl = photoUrl;
    this.address = address;
    this.isOpen = isOpen;
    this.photoHtmlAttribution = photoHtmlAttribution;
    this.coordinates = coordinates;
  }

  public boolean isOpen() {
    return isOpen;
  }

  public GeoCoordinates getCoordinates() {
    return coordinates;
  }

  public String[] getPhotoHtmlAttribution() {
    return photoHtmlAttribution;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getStars() {
    return stars;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public String getAddress() {
    return address;
  }

  ////////////////// --- INNERS ---///////////////////////

  public enum Type {
    RESTAURANT;
  }

  public enum Field {
    NAME,
    GEO_COORDINATES,
    ADDRESS,
    PHONE_NUMBER,
    WEBSITE_URL,
    RATE,
    OPENING_HOURS,
    PHOTO_URL
  }

  public enum LangCode {
    fr,
    en;

    public static LangCode getSystemLanguage(){
      if(Locale.getDefault().getLanguage().equals("fr"))
        return fr;
      return en;
    }
  }
}
