package com.berete.go4lunch.di;

import com.berete.go4lunch.data_sources.restaurants.remote_source.GooglePlacesAPIClient;
import com.berete.go4lunch.domain.restaurants.services.PlaceDetailsProvider;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;

@Module
@InstallIn(ServiceComponent.class)
public abstract class ServiceModuleForInterfaces {

  @Binds
  public abstract PlaceDetailsProvider bindPlaceDetailsProvider(
      GooglePlacesAPIClient googlePlacesAPIClient
  );
}
