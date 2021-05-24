package com.berete.go4lunch.di;

import com.berete.go4lunch.data_souces.shared.remote_source.FirebaseServicesClient;
import com.berete.go4lunch.domain.shared.UserProvider;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class SingletonModuleForInterfaces {

  @Binds
  @Singleton
  public abstract UserProvider bindUserProvider(
      FirebaseServicesClient firebaseServicesClient
  );

}
