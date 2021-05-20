package com.berete.go4lunch.di;

import com.berete.go4lunch.data_souces.restaurants.remote_source.GoogleNearbyPlaceAPIClient;
import com.berete.go4lunch.data_souces.restaurants.remote_source.PlaceHttpClient;


import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(ViewModelComponent.class)
public class ViewModelModule {

    @Provides
    @ViewModelScoped
    public PlaceHttpClient providePlacesHttpClient(){
        return new Retrofit.Builder()
                .baseUrl(GoogleNearbyPlaceAPIClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PlaceHttpClient.class);
    }

}
