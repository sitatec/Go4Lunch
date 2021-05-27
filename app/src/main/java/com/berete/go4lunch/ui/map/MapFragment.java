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
import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.ui.core.view_models.shared.RestaurantRelatedViewModel;
import com.berete.go4lunch.ui.restaurant.details.RestaurantDetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;

@AndroidEntryPoint
public class MapFragment extends Fragment {

  private static final String LOG_TAG = MapFragment.class.getSimpleName();
  private GoogleMap map;
  private RestaurantRelatedViewModel viewModel;

  @Inject public UserProvider userProvider;
  @Inject public CurrentLocationProvider currentLocationProvider;
  private Map<String, Integer> workmatesCountByRestaurant = new HashMap<>();

  public MapFragment() {}

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
    initViewModel(container);
    final SupportMapFragment mapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this::onMapReady);
    fetchWorkmatesCountByRestaurant();
    return fragmentView;
  }

  private void fetchWorkmatesCountByRestaurant() {
    final String currentUserWorkplace = userProvider.getCurrentUser().getWorkplaceId();
    if (currentUserWorkplace != null) {
      viewModel.getWorkmatesCountByRestaurant(
          currentUserWorkplace,
          new Callback<Map<String, Integer>>() {
            @Override
            public void onSuccess(Map<String, Integer> workmatesCount) {
              Log.d(LOG_TAG, "MAP LOADED SUCCESSFULLY");
              workmatesCountByRestaurant = workmatesCount;
              currentLocationProvider.getCurrentCoordinates(MapFragment.this::updateMapUI);
            }

            @Override
            public void onFailure() {}
          });
    }
  }

  private void initViewModel(View fragmentViewContainer) {
    final NavController navController = Navigation.findNavController(fragmentViewContainer);
    final NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.navigation_graph);
    viewModel =
        new ViewModelProvider(
                backStackEntry,
                HiltViewModelFactory.createInternal(getActivity(), backStackEntry, null, null))
            .get(RestaurantRelatedViewModel.class);
  }

  @SuppressLint("MissingPermission")
  private void onMapReady(GoogleMap map) {
    Log.d(LOG_TAG, "MAP LOADED SUCCESSFULLY");
    this.map = map;
    currentLocationProvider.getCurrentCoordinates(MapFragment.this::updateMapUI);
    map.setOnInfoWindowClickListener(this::displayRestaurantDetails);
  }

  @SuppressLint("MissingPermission")
  private void updateMapUI(GeoCoordinates currentLocation) {
    Log.d(LOG_TAG, "CURRENT_LOCATION = " + currentLocation);
    if (currentLocation != null) {
      map.setMyLocationEnabled(true);
      map.getUiSettings().setMyLocationButtonEnabled(true);
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordToLatLog(currentLocation), 14));
      showNearbyRestaurants(currentLocation);
    } else {
      map.getUiSettings().setMyLocationButtonEnabled(false);
      showErrorMessage();
    }
  }
  private void showNearbyRestaurants(GeoCoordinates currentLocation) {
    viewModel.getNearbyRestaurants(
        currentLocation,
        new Callback<Place[]>() {
          @Override
          public void onSuccess(Place[] places) {
            final Restaurant[] restaurants = (Restaurant[]) places;
            if(restaurants != null) showRestaurantsMarkers(restaurants);
          }

          @Override
          public void onFailure() {}
        });
  }

  private void showRestaurantsMarkers(Restaurant[] restaurants) {
    map.clear();
    int currentMarkerDrawable;
    for (Restaurant restaurant : restaurants) {
      if (workmatesCountByRestaurant.containsKey(restaurant.getId())) {
        currentMarkerDrawable = R.drawable.green_restaurant_pointer;
      } else
        currentMarkerDrawable = R.drawable.restaurant_pointer;
      map.addMarker(
              new MarkerOptions()
                  .position(coordToLatLog(restaurant.getCoordinates()))
                  .title(restaurant.getName())
                  .snippet(restaurant.getAddress())
                  .icon(BitmapDescriptorFactory.fromResource(currentMarkerDrawable)))
          .setTag(restaurant.getId());
    }
  }

  private LatLng coordToLatLog(GeoCoordinates geoCoordinates) {
    return new LatLng(geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
  }

  private void displayRestaurantDetails(Marker clickedMarker) {
    RestaurantDetailsActivity.navigate(clickedMarker.getTag().toString(), getActivity());
  }

  private void showErrorMessage() {
    // TODO implement a view that show to the user that the location permission is not granted. The
    //  view will show a button that will trigger the location request
    //  `currentLocationProvider.getCurrentCoordinates(this::updateMapUI);`
  }

}
