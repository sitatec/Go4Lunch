package com.berete.go4lunch.data_souces.restaurants.remote_source;

import com.berete.go4lunch.data_souces.restaurants.data_objects.PlacesHttpResponseImpl;
import com.berete.go4lunch.domain.restaurants.services.PlaceDataProvider;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.BuildConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class GooglePlacesAPIClient implements PlaceDataProvider {

  public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";

  private final PlacesHttpClient placesHttpClient;
  private Map<String, String> queryParams;

  @Inject
  public GooglePlacesAPIClient(PlacesHttpClient placesHttpClient) {
    this.placesHttpClient = placesHttpClient;
  }

  @Override
  public void setQueryParameters(
      Place.Type[] placeTypes,
      Place.Field[] placeFields,
      GeoCoordinates searchArea,
      Place.LangCode langCode,
      Integer maxDistanceInMeter) {
    queryParams = new HashMap<>();
    queryParams.put("type", placeTypes[0].name().toLowerCase());
    queryParams.put("location", searchArea.toString());
    queryParams.put("language", langCode.name());
    queryParams.put("radius", maxDistanceInMeter.toString());
    queryParams.put("key", BuildConfig.GOOGLE_PLACES_API_KEY);
  }

  @Override
  public void setQueryParameters(
      Place.Type[] placeTypes,
      Place.Field[] placeFields,
      GeoCoordinates searchArea,
      Place.LangCode langCode) {
    setQueryParameters(placeTypes, placeFields, searchArea, langCode, 1000);
  }

  @Override
  public void getPlaceData(Callback callback) {
    assert queryParams != null;
    placesHttpClient
        .get(queryParams)
        .enqueue(
            new retrofit2.Callback<PlacesHttpResponseImpl>() {
              @Override
              public void onResponse(
                      Call<PlacesHttpResponseImpl> call, Response<PlacesHttpResponseImpl> response) {
                final PlacesHttpResponseImpl body = response.body();
                if (body == null) callback.onFailure();
                else callback.onSuccess(body.getPlaces());
              }

              @Override
              public void onFailure(Call<PlacesHttpResponseImpl> call, Throwable t) {
                callback.onFailure();
              }
            });
  }
}
