package com.berete.go4lunch.data_souces.restaurants.remote_source;

import com.berete.go4lunch.data_souces.restaurants.data_objects.AutocompleteHttpResponse;
import com.berete.go4lunch.data_souces.restaurants.data_objects.NearbySearchHttpResponse;
import com.berete.go4lunch.data_souces.restaurants.data_objects.PlaceDetailsHttpResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface PlaceHttpClient {
    @GET("nearbysearch/json")
    Call<NearbySearchHttpResponse> getPlaces(@QueryMap Map<String, String> params);

    @GET("autocomplete/json?strictbounds")
    Call<AutocompleteHttpResponse> getPredictions(@QueryMap Map<String, String> params);

    @GET("details/json")
    Call<PlaceDetailsHttpResponse> getPlaceDetails(@QueryMap Map<String, String> params);
}
