package com.berete.go4lunch.di;

import com.berete.go4lunch.domain.restaurants.services.CurrentLocationProvider;
import com.berete.go4lunch.ui.core.services.location.FusedLocationAdapter;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public abstract class ActivityModuleForInterfaces {

    @Binds
    public abstract CurrentLocationProvider bindCurrentLocationProvider(
            FusedLocationAdapter fusedLocationAdapter
    );
}
