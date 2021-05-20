package com.berete.go4lunch.domain.restaurants.models;

import java.util.Locale;

public class Place {

  private final String id;
  private final String name;
  private final double stars;
  private final String address;
  private boolean isOpen;
  private String mainPhotoUrl;
  private GeoCoordinates coordinates;
  private String websiteUrl;
  private String phoneNumber;
  private Photo[] allPhotos;

  public Place(
      String id,
      String name,
      Double stars,
      String mainPhotoUrl,
      String address,
      boolean isOpen,
      GeoCoordinates coordinates) {
    this.id = id;
    this.name = name;
    this.stars = stars;
    this.mainPhotoUrl = mainPhotoUrl;
    this.address = address;
    this.isOpen = isOpen;
    this.coordinates = coordinates;
  }
  public Place(
      String id,
      String name,
      Double stars,
      Photo[] allPhotos,
      String address,
      String websiteUrl,
      String phoneNumber) {
    this.id = id;
    this.name = name;
    this.stars = stars;
    this.allPhotos = allPhotos;
    this.address = address;
    this.websiteUrl = websiteUrl;
    this.phoneNumber = phoneNumber;
  }

  // To make casting to a child easy.
  public Place(Place copy){
    this(
        copy.getId(),
        copy.getName(),
        copy.getStars(),
        copy.getMainPhotoUrl(),
        copy.getAddress(),
        copy.isOpen(),
        copy.getCoordinates());
    allPhotos = copy.getAllPhotos();
    websiteUrl = copy.getWebsiteUrl();
    phoneNumber = copy.getPhoneNumber();
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }
  public boolean isOpen() {
    return isOpen;
  }

  public GeoCoordinates getCoordinates() {
    return coordinates;
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

  public String getMainPhotoUrl() {
    return mainPhotoUrl;
  }

  public String getAddress() {
    return address;
  }

  public Photo[] getAllPhotos() {
    return allPhotos;
  }

  ////////////////// --- INNERS ---///////////////////////


  public static class Photo {

    private final String[] attributions;
    private final String url;

    public Photo(String url, String[] attributions) {
      this.url = url;
      this.attributions = attributions;
    }

    public String[] getAttributions() {
      return attributions;
    }

    public String getUrl() {
      return url;
    }
  }

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
