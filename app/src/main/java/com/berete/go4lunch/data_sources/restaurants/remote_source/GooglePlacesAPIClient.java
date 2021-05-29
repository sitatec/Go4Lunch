package com.berete.go4lunch.data_sources.restaurants.remote_source;

import com.berete.go4lunch.BuildConfig;
import com.berete.go4lunch.data_sources.restaurants.data_objects.AutocompleteHttpResponse;
import com.berete.go4lunch.data_sources.restaurants.data_objects.NearbySearchHttpResponse;
import com.berete.go4lunch.data_sources.restaurants.data_objects.PlaceDetailsHttpResponse;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;
import com.berete.go4lunch.domain.restaurants.services.NearbyPlaceProvider;
import com.berete.go4lunch.domain.restaurants.services.PlaceDetailsProvider;
import com.berete.go4lunch.domain.utils.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class GooglePlacesAPIClient
    implements NearbyPlaceProvider, AutocompleteService, PlaceDetailsProvider {

  public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

  private final PlaceHttpClient placeHttpClient;
  private Map<String, String> placeQueryParams;
  private Map<String, String> autocompleteQueryParams;
  private final AutocompleteSessionTokenHandler autocompleteSessionTokenHandler;

  @Inject
  public GooglePlacesAPIClient(
      PlaceHttpClient placeHttpClient,
      AutocompleteSessionTokenHandler autocompleteSessionTokenHandler) {
    this.placeHttpClient = placeHttpClient;
    this.autocompleteSessionTokenHandler = autocompleteSessionTokenHandler;
  }

  // ############################################################ //
  // ################ ----  NEARBY_SEARCH  ----- ################ //
  // ############################################################ //

  @Override
  public void setNearbySearchQueryParams(
      Place.Type[] placeTypes,
      Place.Field[] placeFields,
      GeoCoordinates searchArea,
      Place.LangCode langCode,
      Integer maxDistanceInMeter) {
    placeQueryParams = new HashMap<>();
    placeQueryParams.put("type", placeTypes[0].name().toLowerCase());
    placeQueryParams.put("location", searchArea.toString());
    placeQueryParams.put("language", langCode.name());
    placeQueryParams.put("radius", maxDistanceInMeter.toString());
    placeQueryParams.put("key", BuildConfig.GOOGLE_PLACE_API_KEY);
  }

  @Override
  public void setNearbySearchQueryParams(
      Place.Type[] placeTypes,
      Place.Field[] placeFields,
      GeoCoordinates searchArea,
      Place.LangCode langCode) {
    setNearbySearchQueryParams(placeTypes, placeFields, searchArea, langCode, DEFAULT_SEARCH_RADIUS);
  }

  @Override
  public void getPlaceData(Callback callback) {
    assert placeQueryParams != null;
    placeHttpClient.getPlaces(placeQueryParams).enqueue(getPlaceResponseCallback(callback));
  }

  private retrofit2.Callback<NearbySearchHttpResponse> getPlaceResponseCallback(Callback callback) {
    return new retrofit2.Callback<NearbySearchHttpResponse>() {
      @Override
      public void onResponse(
          Call<NearbySearchHttpResponse> call, Response<NearbySearchHttpResponse> response) {
        final NearbySearchHttpResponse body = response.body();
        if (body == null) callback.onFailure();
        else callback.onSuccess(body.getPlaces());
      }

      @Override
      public void onFailure(Call<NearbySearchHttpResponse> call, Throwable t) {
        callback.onFailure();
      }
    };
  }

  // ############################################################ //
  // ################ ----  AUTOCOMPLETE  ----- ################# //
  // ############################################################ //

  @Override
  public void predict(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Integer radiusInMeter,
      Callback<Prediction[]> listener) {
    buildAutocompleteQuery(input, currentLocation, langCode, radiusInMeter);
    placeHttpClient
        .getPredictions(autocompleteQueryParams)
        .enqueue(getAutocompleteResponseListener(listener, null));
  }

  @Override
  public void predict(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Callback<Prediction[]> listener) {
    predict(input, currentLocation, langCode, DEFAULT_COVERED_RADIUS, listener);
  }

  @Override
  public void predictWithFilter(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Integer radiusInMeter,
      Place.Type[] filter,
      Callback<Prediction[]> listener) {
    buildAutocompleteQuery(input, currentLocation, langCode, radiusInMeter);
    placeHttpClient
        .getPredictions(autocompleteQueryParams)
        .enqueue(getAutocompleteResponseListener(listener, filter));
  }

  @Override
  public void predictWithFilter(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Place.Type[] filter,
      Callback<Prediction[]> listener) {
    predictWithFilter(input, currentLocation, langCode, DEFAULT_COVERED_RADIUS, filter, listener);
  }

  private void buildAutocompleteQuery(
      String input,
      GeoCoordinates currentLocation,
      Place.LangCode langCode,
      Integer radiusInMeter) {
    autocompleteQueryParams = new HashMap<>();
    autocompleteQueryParams.put("key", BuildConfig.GOOGLE_PLACE_API_KEY);
    autocompleteQueryParams.put("types", "establishment");
    autocompleteQueryParams.put("input", input);
    autocompleteQueryParams.put("language", langCode.name());
    autocompleteQueryParams.put("sessiontoken", autocompleteSessionTokenHandler.getToken());
    if(currentLocation != null) {
      autocompleteQueryParams.put("origin", currentLocation.toString());
      autocompleteQueryParams.put("location", currentLocation.toString());
      autocompleteQueryParams.put("radius", radiusInMeter.toString());
      autocompleteQueryParams.put("strictbounds", "");
    }
  }

  private retrofit2.Callback<AutocompleteHttpResponse> getAutocompleteResponseListener(
      Callback<Prediction[]> listener, Place.Type[] filter) {
    return new retrofit2.Callback<AutocompleteHttpResponse>() {
      @Override
      public void onResponse(
          Call<AutocompleteHttpResponse> call, Response<AutocompleteHttpResponse> response) {
        final AutocompleteHttpResponse responseBody = response.body();
        if (responseBody == null) listener.onFailure();
        else {
          if(filter != null) listener.onSuccess(responseBody.getFilteredPredictions(filter));
          else listener.onSuccess(responseBody.getPredictions());
//          Log.i("HTTP_RESPONSE", "onResponseCallback");
        }
      }

      @Override
      public void onFailure(Call<AutocompleteHttpResponse> call, Throwable t) {
        listener.onFailure();
      }
    };
  }

  // ############################################################ //
  // ################ ----  PLACE_DETAIL  ----- ################# //
  // ############################################################ //

  @Override
  public void getPlaceDetail(
      String placeId,
      Place.Field[] fieldsToReturn,
      Place.LangCode langCode,
      Callback<Place> listener) {
    final Map<String, String> placeDetailsQueryParams = new HashMap<>();
    placeDetailsQueryParams.put("key", BuildConfig.GOOGLE_PLACE_API_KEY);
    placeDetailsQueryParams.put("place_id", placeId);
    placeDetailsQueryParams.put("language", langCode.name());
    placeDetailsQueryParams.put("fields", convertToGoogleApiFields(fieldsToReturn));
    placeDetailsQueryParams.put("sessiontoken", autocompleteSessionTokenHandler.getAndResetToken());
    placeHttpClient
        .getPlaceDetails(placeDetailsQueryParams)
        .enqueue(getDetailsResponseCallback(listener));
  }

  private retrofit2.Callback<PlaceDetailsHttpResponse> getDetailsResponseCallback(
      Callback<Place> listener) {
    return new retrofit2.Callback<PlaceDetailsHttpResponse>() {
      @Override
      public void onResponse(
          Call<PlaceDetailsHttpResponse> call, Response<PlaceDetailsHttpResponse> response) {
        final PlaceDetailsHttpResponse responseBody = response.body();
        if (responseBody != null) {
          final Place placeResult = responseBody.getPlaceDetails();
          if (placeResult != null) {
            listener.onSuccess(placeResult);
          } else listener.onFailure();
        } else listener.onFailure();
      }

      @Override
      public void onFailure(Call<PlaceDetailsHttpResponse> call, Throwable t) {
        listener.onFailure();
      }
    };
  }

  public static String convertToGoogleApiFields(Place.Field[] entries) {
    final StringBuilder stringBuilder = new StringBuilder();
    String currentField;
    for (int i = 0; i < entries.length; i++) {
      currentField = convertField(entries[i]);
      if (currentField != null) {
        stringBuilder.append(currentField);
        if (i < entries.length - 1) stringBuilder.append(",");
      }
    }
    return stringBuilder.toString();
  }

  private static String convertField(Place.Field input) {
    switch (input) {
      case ID:
        return "place_id";
      case NAME:
        return "name";
      case ADDRESS:
        return "formatted_address";
      case PHONE_NUMBER:
        return "formatted_phone_number";
      case WEBSITE_URL:
        return "website";
      case RATE:
        return "rating";
      case OPENING_HOURS:
        return "opening_hours";
      case PHOTO_URL:
        return "photo";
      default:
        return null;
    }
  }

  // ############################################################ //
  // ################ ----  INNER_CLASSES  ----- ################ //
  // ############################################################ //

  public static class AutocompleteSessionTokenHandler {
    private static String token;

    @Inject
    public AutocompleteSessionTokenHandler() {}

    private void renewToken() {
      token = UUID.randomUUID().toString();
      postponeTokenExpiration();
    }

    private void postponeTokenExpiration() {
      Executors.newSingleThreadScheduledExecutor().schedule(this::resetToken, 2, TimeUnit.MINUTES);
    }

    private void resetToken() {
      token = null;
    }

    public String getToken() {
      if (token == null) renewToken();
      else postponeTokenExpiration();
      return token;
    }

    public String getAndResetToken(){
      if(token == null) return "null_token";
      final String tokenCopy = new String(token);
      resetToken();
      return tokenCopy;
    }

  }

  public interface Utils {
    static String photoReferenceToUrl(String reference) {
      return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photoreference="
          + reference
          + "&key="
          + BuildConfig.GOOGLE_PLACE_API_KEY;
    }
  }
}
