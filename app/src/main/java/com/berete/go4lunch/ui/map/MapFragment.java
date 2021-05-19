package com.berete.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.berete.go4lunch.R;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.services.CurrentLocationProvider;
import com.berete.go4lunch.domain.restaurants.services.PlaceDataProvider;
import com.berete.go4lunch.ui.core.shared_view_models.RestaurantViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;

@AndroidEntryPoint
public class MapFragment extends Fragment {

  private static final String LOG_TAG = MapFragment.class.getSimpleName();
  private GoogleMap map;
  private RestaurantViewModel viewModel;

  @Inject public CurrentLocationProvider currentLocationProvider;

  public MapFragment() {}

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
    initViewModel(container);
    final SupportMapFragment mapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this::onMapReady);
    return fragmentView;
  }

  private void initViewModel(View fragmentViewContainer) {
    final NavController navController = Navigation.findNavController(fragmentViewContainer);
    final NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.navigation_graph);
    viewModel =
        new ViewModelProvider(
                backStackEntry,
                HiltViewModelFactory.createInternal(getActivity(), backStackEntry, null, null))
            .get(RestaurantViewModel.class);
  }

  @SuppressLint("MissingPermission")
  private void onMapReady(GoogleMap map) {
    Log.i(LOG_TAG, "MAP LOADED SUCCESSFULLY");
    this.map = map;
    currentLocationProvider.getCurrentCoordinates(this::updateMapUI);
  }

  @SuppressLint("MissingPermission")
  private void updateMapUI(GeoCoordinates currentLocation) {
    if (currentLocation != null) {
      map.setMyLocationEnabled(true);
      map.getUiSettings().setMyLocationButtonEnabled(true);
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordToLatLog(currentLocation), 15));
      showNearbyRestaurants(currentLocation);
    } else {
      map.getUiSettings().setMyLocationButtonEnabled(false);
      showErrorMessage();
    }
  }

  private void showErrorMessage() {
    // TODO implement a view that show to the user that the location permission is not granted. The
    //  view will show a button that will trigger the location request
    //  `currentLocationProvider.getCurrentCoordinates(this::updateMapUI);`
  }

  private void showNearbyRestaurants(GeoCoordinates currentLocation) {
    viewModel.getNearbyRestaurants(
        currentLocation,
        new PlaceDataProvider.Callback() {
          @Override public void onSuccess(Place[] places) {
            map.clear();
            final Restaurant[] restaurants = (Restaurant[]) places;
            for (Restaurant restaurant : restaurants) {
              map.addMarker(new MarkerOptions()
                      .position(coordToLatLog(restaurant.getCoordinates()))
                      .title(restaurant.getName()));
            }
          }

          @Override public void onFailure() {}
        });
  }

  private LatLng coordToLatLog(GeoCoordinates geoCoordinates) {
    return new LatLng(geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
  }
}
