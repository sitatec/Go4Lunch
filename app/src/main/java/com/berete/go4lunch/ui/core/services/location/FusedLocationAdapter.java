package com.berete.go4lunch.ui.core.services.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.services.CurrentLocationProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class FusedLocationAdapter implements CurrentLocationProvider {

  private final FusedLocationProviderClient fusedLocationProviderClient;
  private final LocationPermissionHandler locationPermissionHandler;

  @Inject
  public FusedLocationAdapter(
      @ActivityContext Context context, LocationPermissionHandler locationPermissionHandler) {
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    this.locationPermissionHandler = locationPermissionHandler;
  }

  @Override
  @SuppressLint("MissingPermission")
  public void getCurrentCoordinates(OnCoordinatesResultListener listener) {
    if (locationPermissionHandler.hasPermission()) {
        fusedLocationProviderClient
          .getLastLocation()
          .addOnCompleteListener(
              task -> {
                final Location location = task.getResult();
                if (task.isSuccessful() && location != null) {
                  listener.onResult(
                      new GeoCoordinates(location.getLatitude(), location.getLongitude()));
                } else listener.onResult(null);
              });
    } else locationPermissionHandler.requestPermission(() -> getCurrentCoordinates(listener));
  }
}
