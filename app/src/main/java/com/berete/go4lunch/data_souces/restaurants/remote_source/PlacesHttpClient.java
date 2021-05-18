package com.berete.go4lunch.data_souces.restaurants.remote_source;

import com.berete.go4lunch.data_souces.restaurants.data_objects.PlacesHttpResponseImpl;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface PlacesHttpClient {
    @GET("json")
    Call<PlacesHttpResponseImpl> get(@QueryMap Map<String, String> params);
}
