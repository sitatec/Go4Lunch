package com.berete.go4lunch.di;

import com.berete.go4lunch.data_souces.restaurants.remote_source.GooglePlacesAPIClient;
import com.berete.go4lunch.data_souces.restaurants.remote_source.PlaceHttpClient;
import com.berete.go4lunch.data_souces.shared.remote_source.FirebaseServicesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class SingletonModule {

  @Provides
  @Singleton
  public FirebaseServicesClient provideFirebaseAdminClient() {
    return new FirebaseServicesClient(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
  }

  @Provides
  @Singleton
  public PlaceHttpClient providePlacesHttpClient() {
    return new Retrofit.Builder()
        .baseUrl(GooglePlacesAPIClient.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PlaceHttpClient.class);
  }
}
