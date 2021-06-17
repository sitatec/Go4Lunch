package com.berete.go4lunch.data_sources.restaurants.dto;

import com.berete.go4lunch.data_sources.restaurants.remote_source.GooglePlacesAPIClient;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class NearbySearchHttpResponse {
  @SerializedName("results")
  @Expose
  public List<PlaceDataObject> result;

  @SerializedName("status")
  @Expose
  public String status;

  public Place[] getPlaces() {
    Place[] places = new Place[0];
    if (status.equals("OK")) {
      places = result.stream()
          .map(PlaceDataObject::toPlace)
          .filter(Objects::nonNull)
          .toArray(Place[]::new);
    }
    return places;
  }
}

class PlaceDataObject {
  @SerializedName("business_status")
  public String business_status;

  @SerializedName("geometry")
  @Expose
  public Geometry geometry;

  @SerializedName("icon")
  public String icon;

  @SerializedName("name")
  @Expose
  public String name;

  @SerializedName("opening_hours")
  @Expose
  public Opening_hours opening_hours;

  @SerializedName("photos")
  @Expose
  public List<Photo> photos;

  @SerializedName("place_id")
  @Expose
  public String place_id;

  @SerializedName("plus_code")
  @Expose
  public Plus_code plus_code;

  @SerializedName("price_level")
  public Integer price_level;

  @SerializedName("rating")
  @Expose
  public Double rating;

  @SerializedName("reference")
  public String reference;

  @SerializedName("scope")
  public String scope;

  @SerializedName("types")
  public List<String> types;

  @SerializedName("user_ratings_total")
  public Integer user_ratings_total;

  @SerializedName("vicinity")
  @Expose
  public String vicinity;

  public Place toPlace() {
    Place place;
    try {
      place =
          new Place(
              place_id,
              name,
              rating,
              GooglePlacesAPIClient.Utils.photoReferenceToUrl(photos.get(0).photo_reference),
              vicinity,
              opening_hours.open_now,
              geometry.location.toGeoCoordinates(), icon);
    } catch (Exception e) {
      place = null;
    }
    return place;
  }
}

class Southwest {
  @SerializedName("lat")
  @Expose
  public Double lat;

  @SerializedName("lng")
  @Expose
  public Double lng;
}

class Plus_code {
  @SerializedName("compound_code")
  @Expose
  public String compound_code;

  @SerializedName("global_code")
  @Expose
  public String global_code;

}

class Viewport {
  @SerializedName("northeast")
  @Expose
  public Northeast northeast;

  @SerializedName("southwest")
  @Expose
  public Southwest southwest;

}

class Geometry {
  @SerializedName("location")
  @Expose
  public Location location;

  @SerializedName("viewport")
  @Expose
  public Viewport viewport;

}

class Opening_hours {
  @SerializedName("open_now")
  @Expose
  public Boolean open_now;

}

class Photo {
  @SerializedName("height")
  @Expose
  public Integer height;

  @SerializedName("html_attributions")
  @Expose
  public List<String> html_attributions = null;

  @SerializedName("photo_reference")
  @Expose
  public String photo_reference;

  @SerializedName("width")
  @Expose
  public Integer width;

}

class Northeast {
  @SerializedName("lat")
  @Expose
  public Double lat;

  @SerializedName("lng")
  @Expose
  public Double lng;

}

class Location {
  @SerializedName("lat")
  @Expose
  public Double lat;

  @SerializedName("lng")
  @Expose
  public Double lng;


  public GeoCoordinates toGeoCoordinates() {
    return new GeoCoordinates(lat, lng);
  }
}
