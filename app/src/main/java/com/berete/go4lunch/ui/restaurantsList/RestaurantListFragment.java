package com.berete.go4lunch.ui.restaurantsList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.FragmentRestaurantsListBinding;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.services.CurrentLocationProvider;
import com.berete.go4lunch.domain.restaurants.services.PlaceDataProvider;
import com.berete.go4lunch.ui.core.shared_view_models.RestaurantViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;

@AndroidEntryPoint
public class RestaurantListFragment extends Fragment {

  @Inject public CurrentLocationProvider currentLocationProvider;
  public RestaurantViewModel restaurantViewModel;
  private FragmentRestaurantsListBinding binding;

  public RestaurantListFragment() {}

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentRestaurantsListBinding.inflate(inflater, container, false);
    final RecyclerView recyclerView = binding.restaurantsList;
    initializeViewModel(container);
    currentLocationProvider.getCurrentCoordinates(
        currentLocation -> {
          restaurantViewModel.getNearbyRestaurants(
              currentLocation,
              new PlaceDataProvider.Callback() {
                @Override
                public void onSuccess(Place[] places) {
                  recyclerView.setAdapter(new RestaurantListAdapter((Restaurant[]) places, currentLocation));
                  recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                  binding.restaurantListShimmer.stopShimmer();
                  binding.restaurantListShimmer.setVisibility(View.GONE);
                }

                @Override
                public void onFailure() {}
              });
        });
    return binding.getRoot();
  }


  private void initializeViewModel(View fragmentViewContainer) {
    final NavBackStackEntry backStackEntry =
        Navigation.findNavController(fragmentViewContainer)
            .getBackStackEntry(R.id.navigation_graph);
    restaurantViewModel =
        new ViewModelProvider(
                backStackEntry,
                HiltViewModelFactory.createInternal(getActivity(), backStackEntry, null, null))
            .get(RestaurantViewModel.class);
  }
}
