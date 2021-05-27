package com.berete.go4lunch.ui.core.services.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.services.CurrentLocationProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;

import javax.inject.Inject;

// TODO Refactoring
public class FusedLocationAdapter implements CurrentLocationProvider {

  private final FusedLocationProviderClient fusedLocationProviderClient;
  private final LocationPermissionHandler locationPermissionHandler;
  private Location lastKnownLocation;
  private OnCoordinatesResultListener onResultListener;

  @Inject
  public FusedLocationAdapter(
      Activity activity, LocationPermissionHandler locationPermissionHandler) {
    this.locationPermissionHandler = locationPermissionHandler;
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    warmUpTheLocationProvider();
  }

  @Override
  @SuppressLint("MissingPermission")
  public void getCurrentCoordinates(OnCoordinatesResultListener listener) {
    if (locationPermissionHandler.hasPermission()) {
      fusedLocationProviderClient
          .getLastLocation()
          .addOnCompleteListener(getOnCompleteListener(listener));
    } else locationPermissionHandler.requestPermission(() -> getCurrentCoordinates(listener));
  }

  private OnCompleteListener<Location> getOnCompleteListener(OnCoordinatesResultListener listener) {
    return task -> {
      final Location location = task.getResult();
      if (task.isSuccessful() && location != null) {
        listener.onResult(locationToGeoCoordinates(location));
        lastKnownLocation = location;
      } else if (lastKnownLocation != null) {
        listener.onResult(locationToGeoCoordinates(lastKnownLocation));
      } else onResultListener = listener;
    };
  }

  /**
   * This method make sure that the a location has been requested at least once before requesting
   * the last know location, otherwise the last know location could always return null until a
   * client connect and request the current location.
   */
  @SuppressLint("MissingPermission")
  private void warmUpTheLocationProvider() {
    final LocationRequest locationRequest =
        LocationRequest.create()
            .setInterval(100)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    if (locationPermissionHandler.hasPermission()) {
      fusedLocationProviderClient.requestLocationUpdates(
          locationRequest,
          new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
              super.onLocationResult(locationResult);
              lastKnownLocation = locationResult.getLastLocation();
              if (onResultListener != null) {
                onResultListener.onResult(locationToGeoCoordinates(lastKnownLocation));
              }
              fusedLocationProviderClient.removeLocationUpdates(this);
            }
          },
          Looper.getMainLooper());
    } else locationPermissionHandler.requestPermission(this::warmUpTheLocationProvider);
  }

  private GeoCoordinates locationToGeoCoordinates(Location location) {
    return new GeoCoordinates(location.getLatitude(), location.getLongitude());
  }
}
