package com.berete.go4lunch.di;

import com.berete.go4lunch.data_souces.restaurants.remote_source.GooglePlacesAPIClient;
import com.berete.go4lunch.domain.restaurants.services.PlaceDataProvider;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
public abstract class ViewModelModuleForInterfaces {

    @Binds
    public abstract PlaceDataProvider bindPlaceDataProvider(
        GooglePlacesAPIClient googlePlacesAPIClient
    );

}
