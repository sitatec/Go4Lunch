package com.berete.go4lunch.data_souces.restaurants.data_objects;

import com.berete.go4lunch.data_souces.restaurants.remote_source.GoogleNearbyPlaceAPIClient.Utils;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailsHttpResponse {
  @SerializedName("html_attributions")
  @Expose
  public List<Object> html_attributions = null;

  @SerializedName("result")
  @Expose
  public PlaceDetailsDataObject placeDetailsDataObject;

  @SerializedName("status")
  @Expose
  public String status;

  public Place getPlaceDetails() {
    if (status.equals("OK")) {
      return new Place(
          placeDetailsDataObject.place_id,
          placeDetailsDataObject.name,
          placeDetailsDataObject.rating,
          getPhotos(),
          placeDetailsDataObject.formatted_address,
          placeDetailsDataObject.website,
          placeDetailsDataObject.formatted_phone_number);
    }
    return null;
  }

  private Place.Photo[] getPhotos() {
    final List<Place.Photo> photoList = new ArrayList<>();
    for (Photo currentPhoto : placeDetailsDataObject.photos) {
      photoList.add(
          new Place.Photo(
              Utils.photoReferenceToUrl(currentPhoto.photo_reference),
              currentPhoto.html_attributions));
    }
    return photoList.toArray(new Place.Photo[0]);
  }

  private static class PlaceDetailsDataObject {
    @SerializedName("formatted_address")
    @Expose
    public String formatted_address;

    @SerializedName("formatted_phone_number")
    @Expose
    public String formatted_phone_number;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("photos")
    @Expose
    public List<Photo> photos = null;

    @SerializedName("place_id")
    @Expose
    public String place_id;

    @SerializedName("rating")
    @Expose
    public Double rating;

    @SerializedName("website")
    @Expose
    public String website;
  }

  private static class Photo {
    @SerializedName("height")
    public Integer height;

    @SerializedName("html_attributions")
    @Expose
    public String[] html_attributions = null;

    @SerializedName("photo_reference")
    @Expose
    public String photo_reference;

    @SerializedName("width")
    public Integer width;
  }
}
