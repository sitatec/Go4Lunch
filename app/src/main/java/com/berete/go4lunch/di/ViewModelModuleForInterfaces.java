package com.berete.go4lunch.di;

import com.berete.go4lunch.data_souces.restaurants.remote_source.GoogleNearbyPlaceAPIClient;
import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;
import com.berete.go4lunch.domain.restaurants.services.NearbyPlaceProvider;

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
      GoogleNearbyPlaceAPIClient googlePlaceAPIClient);

  @Binds
  @ViewModelScoped
  public abstract AutocompleteService bindAutocompleteService(
      GoogleNearbyPlaceAPIClient googlePlaceAPIClient);

}
