package com.berete.go4lunch.data_souces.restaurants.data_objects;

import com.berete.go4lunch.BuildConfig;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlacesHttpResponseImpl implements PlacesHttpResponse {
  @SerializedName("results")
  @Expose
  public List<PlaceDataObject> result;

  public Place[] getPlaces() {
    final List<Place> placeList = new ArrayList<>();
    Place place;
    for (int i = 0; i < result.size(); i++){
      place = result.get(i).toPlace();
      if(place != null) placeList.add(place);
    }
    return placeList.toArray(new Place[0]);
  }

  public static class PlaceDataObject {
    @SerializedName("business_status")
    @Expose
    public String business_status;

    @SerializedName("geometry")
    @Expose
    public Geometry geometry;

    @SerializedName("icon")
    @Expose
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
    @Expose
    public Integer price_level;

    @SerializedName("rating")
    @Expose
    public Double rating;

    @SerializedName("reference")
    @Expose
    public String reference;

    @SerializedName("scope")
    @Expose
    public String scope;

    @SerializedName("types")
    @Expose
    public List<String> types;

    @SerializedName("user_ratings_total")
    @Expose
    public Integer user_ratings_total;

    @SerializedName("vicinity")
    @Expose
    public String vicinity;

    public PlaceDataObject(
        String business_status,
        Geometry geometry,
        String icon,
        String name,
        Opening_hours opening_hours,
        List<Photo> photos,
        String place_id,
        Plus_code plus_code,
        Integer price_level,
        Double rating,
        String reference,
        String scope,
        List<String> types,
        Integer user_ratings_total,
        String vicinity) {
      this.business_status = business_status;
      this.geometry = geometry;
      this.icon = icon;
      this.name = name;
      this.opening_hours = opening_hours;
      this.photos = photos;
      this.place_id = place_id;
      this.plus_code = plus_code;
      this.price_level = price_level;
      this.rating = rating;
      this.reference = reference;
      this.scope = scope;
      this.types = types;
      this.user_ratings_total = user_ratings_total;
      this.vicinity = vicinity;
    }

    public Place toPlace() {
      Place place;
      try {
        place = new Place(
            place_id,
            name,
            rating,
            getPhotoUrl(),
            vicinity,
            opening_hours.open_now,
            photos.get(0).html_attributions.toArray(new String[0]),
            geometry.location.toGeoCoordinates());
      } catch (Exception e){
        place = null;
      }
      return place;
    }

    private String getPhotoUrl() {
      return "https://maps.googleapis.com/maps/api/place/photo?maxheight=700&photoreference="
          + photos.get(0).photo_reference
          + "&key="
          + BuildConfig.GOOGLE_PLACES_API_KEY;
    }
  }

  public static class Southwest {
    @SerializedName("lat")
    @Expose
    public Double lat;

    @SerializedName("lng")
    @Expose
    public Double lng;

    public Southwest(Double lat, Double lng) {
      this.lat = lat;
      this.lng = lng;
    }
  }

  public static class Plus_code {
    @SerializedName("compound_code")
    @Expose
    public String compound_code;

    @SerializedName("global_code")
    @Expose
    public String global_code;

    public Plus_code(String compound_code, String global_code) {
      this.compound_code = compound_code;
      this.global_code = global_code;
    }
  }

  public static class Viewport {
    @SerializedName("northeast")
    @Expose
    public Northeast northeast;

    @SerializedName("southwest")
    @Expose
    public Southwest southwest;

    public Viewport(Northeast northeast, Southwest southwest) {
      this.northeast = northeast;
      this.southwest = southwest;
    }
  }

  public static class Geometry {
    @SerializedName("location")
    @Expose
    public Location location;

    @SerializedName("viewport")
    @Expose
    public Viewport viewport;

    public Geometry(Location location, Viewport viewport) {
      this.location = location;
      this.viewport = viewport;
    }
  }

  public static class Opening_hours {
    @SerializedName("open_now")
    @Expose
    public Boolean open_now;

    public Opening_hours(Boolean open_now) {
      this.open_now = open_now;
    }
  }

  public static class Photo {
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

    public Photo(
        Integer height, List<String> html_attributions, String photo_reference, Integer width) {
      this.height = height;
      this.html_attributions = html_attributions;
      this.photo_reference = photo_reference;
      this.width = width;
    }
  }

  public static class Northeast {
    @SerializedName("lat")
    @Expose
    public Double lat;

    @SerializedName("lng")
    @Expose
    public Double lng;

    public Northeast(Double lat, Double lng) {
      this.lat = lat;
      this.lng = lng;
    }
  }

  public static class Location {
    @SerializedName("lat")
    @Expose
    public Double lat;

    @SerializedName("lng")
    @Expose
    public Double lng;

    public Location(Double lat, Double lng) {
      this.lat = lat;
      this.lng = lng;
    }

    public GeoCoordinates toGeoCoordinates() {
      return new GeoCoordinates(lat, lng);
    }
  }
}
