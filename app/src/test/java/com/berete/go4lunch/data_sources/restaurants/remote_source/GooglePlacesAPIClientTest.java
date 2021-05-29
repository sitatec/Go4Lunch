package com.berete.go4lunch.data_sources.restaurants.remote_source;

import com.berete.go4lunch.BuildConfig;
import com.berete.go4lunch.data_sources.restaurants.data_objects.AutocompleteHttpResponse;
import com.berete.go4lunch.data_sources.restaurants.data_objects.NearbySearchHttpResponse;
import com.berete.go4lunch.data_sources.restaurants.data_objects.PlaceDetailsHttpResponse;
import com.berete.go4lunch.data_sources.restaurants.remote_source.GooglePlacesAPIClient.AutocompleteSessionTokenHandler;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;
import com.berete.go4lunch.domain.restaurants.services.NearbyPlaceProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.berete.go4lunch.FakeData.fakeAutocompleteInput;
import static com.berete.go4lunch.FakeData.fakeFilter;
import static com.berete.go4lunch.FakeData.fakeGeoCoordinates;
import static com.berete.go4lunch.FakeData.fakeLangCode;
import static com.berete.go4lunch.FakeData.fakePlaceCallback;
import static com.berete.go4lunch.FakeData.fakePlaceId;
import static com.berete.go4lunch.FakeData.fakePredictionCallback;
import static com.berete.go4lunch.FakeData.fakeSinglePlaceCallback;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GooglePlacesAPIClientTest {

  GooglePlacesAPIClient googlePlacesAPIClient;
  final AutocompleteSessionTokenHandler autocompleteSessionTokenHandler =
      new AutocompleteSessionTokenHandler();

  final Call<NearbySearchHttpResponse> nearbySearchHttpResponseCall = mock(Call.class);
  Response<NearbySearchHttpResponse> nearbySearchHttpResponse;
  NearbySearchHttpResponse nearbySearchHttpResponseValue;

  final Call<AutocompleteHttpResponse> autocompleteHttpResponseCall = mock(Call.class);
  Response<AutocompleteHttpResponse> autocompleteHttpResponse;
  AutocompleteHttpResponse autocompleteHttpResponseValue;

  final Call<PlaceDetailsHttpResponse> placeDetailsHttpResponseCall = mock(Call.class);
  Response<PlaceDetailsHttpResponse> placeDetailsHttpResponse;
  PlaceDetailsHttpResponse placeDetailsHttpResponseValue;

  final PlaceHttpClient fakePlaceHttpClient =
      new PlaceHttpClient() {
        @Override
        public Call<NearbySearchHttpResponse> getPlaces(Map<String, String> params) {
          assertEquals(params.get("key"), BuildConfig.GOOGLE_PLACE_API_KEY);
          assertEquals(params.get("type"), Place.Type.RESTAURANT.name().toLowerCase());
          assertEquals(params.get("location"), fakeGeoCoordinates.toString());
          assertEquals(params.get("language"), fakeLangCode.name());
          assertEquals(
              params.get("radius"), Integer.toString(NearbyPlaceProvider.DEFAULT_SEARCH_RADIUS));
          return nearbySearchHttpResponseCall;
        }

        @Override
        public Call<AutocompleteHttpResponse> getPredictions(Map<String, String> params) {
          assertEquals(params.get("key"), BuildConfig.GOOGLE_PLACE_API_KEY);
          assertEquals(params.get("types"), "establishment");
          assertEquals(params.get("input"), fakeAutocompleteInput);
          assertEquals(params.get("language"), fakeLangCode.name());
          assertEquals(params.get("location"), fakeGeoCoordinates.toString());
          assertEquals(params.get("origin"), fakeGeoCoordinates.toString());
          assertEquals(
              params.get("radius"), Integer.toString(AutocompleteService.DEFAULT_COVERED_RADIUS));
          assertEquals(params.get("sessiontoken"), autocompleteSessionTokenHandler.getToken());
          assertTrue(params.containsKey("strictbounds"));
          return autocompleteHttpResponseCall;
        }

        @Override
        public Call<PlaceDetailsHttpResponse> getPlaceDetails(Map<String, String> params) {
          assertEquals(params.get("key"), BuildConfig.GOOGLE_PLACE_API_KEY);
          assertEquals(params.get("language"), fakeLangCode.name());
          assertEquals(
              params.get("fields"),
              GooglePlacesAPIClient.convertToGoogleApiFields(NearbyPlaceProvider.DEFAULT_FIELDS));
          assertEquals(params.get("place_id"), fakePlaceId);
          assertNotNull(params.get("sessiontoken")); // We can't check the exact value of the token
          // because it should have been reset after a place details query build.
          return placeDetailsHttpResponseCall;
        }
      };

  @Before
  public void setUp() {
    setUpNearbySearchMocks();
    setUpAutocompleteServicesMocks();
    setUpPlaceDetailsMocks();
    googlePlacesAPIClient =
        new GooglePlacesAPIClient(fakePlaceHttpClient, autocompleteSessionTokenHandler);
  }

  // ############################################################ //
  // ################ ----  NEARBY_SEARCH  ----- ################ //
  // ############################################################ //

  @Test
  public void should_build_the_correct_query_parameter_for_the_nearby_search_api() {
    googlePlacesAPIClient.setNearbySearchQueryParams(
        new Place.Type[] {Place.Type.RESTAURANT},
        NearbyPlaceProvider.DEFAULT_FIELDS,
        fakeGeoCoordinates,
        fakeLangCode);
    googlePlacesAPIClient.getPlaceData(fakePlaceCallback);
    // verifications are made in the fakePlaceHttpClient.getPlaces(...) ^ABOVE^
  }

  @Test
  public void should_get_the_nearby_restaurant_data() {
    googlePlacesAPIClient.setNearbySearchQueryParams(
        new Place.Type[] {Place.Type.RESTAURANT},
        NearbyPlaceProvider.DEFAULT_FIELDS,
        fakeGeoCoordinates,
        fakeLangCode);
    googlePlacesAPIClient.getPlaceData(fakePlaceCallback);
    // We need to capture the callback passed to the Call.enqueue(Callback) method, so we will
    // call the onResponse method on that callback an pass it the Response<NearbySearchHttpResponse>
    // we have mocked, and then verify that the NearbySearchHttpResponse.getPlaces() method is
    // called to get the request result. (see the
    // "com.berete.go4lunch.data_sources.restaurants.remote_source.GooglePlacesAPIClient.getPlaceData()" for more understanding).
    final ArgumentCaptor<Callback<NearbySearchHttpResponse>> callbackCaptor =
        ArgumentCaptor.forClass(Callback.class);

    verify(nearbySearchHttpResponseCall).enqueue(callbackCaptor.capture());
    callbackCaptor.getValue().onResponse(nearbySearchHttpResponseCall, nearbySearchHttpResponse);
    verify(nearbySearchHttpResponseValue).getPlaces();
  }

  // ############################################################ //
  // ################ ----  AUTOCOMPLETE  ----- ################# //
  // ############################################################ //

  @Test
  public void should_build_the_correct_query_parameter_for_the_autocomplete_api() {
    googlePlacesAPIClient.predict(
        fakeAutocompleteInput, fakeGeoCoordinates, fakeLangCode, fakePredictionCallback);
    // verifications are made in the fakePlaceHttpClient.getPredictions(...) ^ABOVE^
  }

  @Test
  public void should_return_the_corresponding_predictions_of_the_given_input_text_without_filter() {
    googlePlacesAPIClient.predict(
        fakeAutocompleteInput, fakeGeoCoordinates, fakeLangCode, fakePredictionCallback);
    final ArgumentCaptor<Callback<AutocompleteHttpResponse>> callbackCaptor =
        ArgumentCaptor.forClass(Callback.class);

    verify(autocompleteHttpResponseCall).enqueue(callbackCaptor.capture());
    callbackCaptor.getValue().onResponse(autocompleteHttpResponseCall, autocompleteHttpResponse);
    verify(autocompleteHttpResponseValue).getPredictions();
  }

  @Test
  public void should_return_the_corresponding_predictions_of_the_given_input_text_with_filter() {
    googlePlacesAPIClient.predictWithFilter(
        fakeAutocompleteInput,
        fakeGeoCoordinates,
        fakeLangCode,
        fakeFilter,
        fakePredictionCallback);
    // We need to capture the callback passed to the Call.enqueue(Callback) method, so we will
    // call the onResponse method on that callback an pass it the Response<AutocompleteHttpResponse>
    // we have mocked, and then verify that the
    // AutocompleteHttpResponse.getFilteredPredictions(Place.Type[]) method is called to get the
    // request result. (see the
    // "com.berete.go4lunch.data_sources.restaurants.remote_source.GooglePlacesAPIClient.predictWithFilter(...)" for more understanding).
    final ArgumentCaptor<Callback<AutocompleteHttpResponse>> callbackCaptor =
        ArgumentCaptor.forClass(Callback.class);

    verify(autocompleteHttpResponseCall).enqueue(callbackCaptor.capture());
    callbackCaptor.getValue().onResponse(autocompleteHttpResponseCall, autocompleteHttpResponse);
    verify(autocompleteHttpResponseValue).getFilteredPredictions(fakeFilter);
  }

  // ############################################################ //
  // ################ ----  PLACE_DETAIL  ----- ################# //
  // ############################################################ //

  @Test
  public void should_build_the_correct_query_parameter_for_the_place_detail_api() {
    googlePlacesAPIClient.getPlaceDetail(
        fakePlaceId, NearbyPlaceProvider.DEFAULT_FIELDS, fakeLangCode, fakeSinglePlaceCallback);
    // verifications are made in the fakePlaceHttpClient.getPlaceDetails(...) ^ABOVE^
  }

  @Test
  public void should_get_the_place_detail() {
    googlePlacesAPIClient.getPlaceDetail(
        fakePlaceId, NearbyPlaceProvider.DEFAULT_FIELDS, fakeLangCode, fakeSinglePlaceCallback);
    // We need to capture the callback passed to the Call.enqueue(Callback) method, so we will
    // call the onResponse method on that callback an pass it the Response<PlaceDetailsHttpResponse>
    // we have mocked, and then verify that the PlaceDetailsHttpResponse.getPlaceDetails() method is
    // called to get the request result. (see the
    // "com.berete.go4lunch.data_sources.restaurants.remote_source.GooglePlacesAPIClient.getPlaceDetail(...)" for more understanding).
    final ArgumentCaptor<Callback<PlaceDetailsHttpResponse>> callbackCaptor =
        ArgumentCaptor.forClass(Callback.class);
    verify(placeDetailsHttpResponseCall).enqueue(callbackCaptor.capture());
    callbackCaptor.getValue().onResponse(placeDetailsHttpResponseCall, placeDetailsHttpResponse);
    verify(placeDetailsHttpResponseValue).getPlaceDetails();
  }

  // -------------  Utils ------------- //

  private void setUpNearbySearchMocks() {
    nearbySearchHttpResponse = mock(Response.class);
    nearbySearchHttpResponseValue = mock(NearbySearchHttpResponse.class);
    when(nearbySearchHttpResponse.body()).thenReturn(nearbySearchHttpResponseValue);
  }

  private void setUpAutocompleteServicesMocks() {
    autocompleteHttpResponse = mock(Response.class);
    autocompleteHttpResponseValue = mock(AutocompleteHttpResponse.class);
    when(autocompleteHttpResponse.body()).thenReturn(autocompleteHttpResponseValue);
  }

  private void setUpPlaceDetailsMocks() {
    placeDetailsHttpResponse = mock(Response.class);
    placeDetailsHttpResponseValue = mock(PlaceDetailsHttpResponse.class);
    when(placeDetailsHttpResponse.body()).thenReturn(placeDetailsHttpResponseValue);
  }
}
