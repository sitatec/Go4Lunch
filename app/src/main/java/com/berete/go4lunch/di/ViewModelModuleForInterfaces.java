package com.berete.go4lunch.di;

import com.berete.go4lunch.data_souces.restaurants.remote_source.GooglePlacesAPIClient;
import com.berete.go4lunch.data_souces.shared.remote_source.FirebaseServicesClient;
import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;
import com.berete.go4lunch.domain.restaurants.services.NearbyPlaceProvider;
import com.berete.go4lunch.domain.restaurants.services.PlaceDetailsProvider;
import com.berete.go4lunch.domain.restaurants.services.RestaurantSpecificDataProvider;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public abstract class ViewModelModuleForInterfaces {

  @Binds
  @ViewModelScoped
  public abstract NearbyPlaceProvider bindPlaceDataProvider(
      GooglePlacesAPIClient googlePlaceAPIClient);

  @Binds
  @ViewModelScoped
  public abstract AutocompleteService bindAutocompleteService(
      GooglePlacesAPIClient googlePlaceAPIClient);

  @Binds
  @ViewModelScoped
  public abstract PlaceDetailsProvider bindPlaceDetailsProvider(
      GooglePlacesAPIClient googlePlacesAPIClient
  );

  @Binds
  @ViewModelScoped
  public abstract RestaurantSpecificDataProvider bind(
    FirebaseServicesClient firebaseServicesClient
  );

}
