package com.berete.go4lunch.ui.restaurant.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.FragmentRestaurantsListBinding;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.restaurants.services.CurrentLocationProvider;
import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.domain.utils.DistanceUtils;
import com.berete.go4lunch.ui.core.view_models.shared.RestaurantRelatedViewModel;
import com.berete.go4lunch.ui.restaurant.details.RestaurantDetailsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;

@AndroidEntryPoint
public class RestaurantListFragment extends Fragment {

  @Inject public CurrentLocationProvider currentLocationProvider;
  @Inject public UserProvider userProvider;
  public RestaurantRelatedViewModel restaurantRelatedViewModel;
  private FragmentRestaurantsListBinding binding;
  private GeoCoordinates currentLocation;
  private Map<String, Integer> workmatesCountByRestaurant = new HashMap<>();
  private List<Restaurant> restaurants = new ArrayList<>();
  private final RestaurantListAdapter listAdapter =
      new RestaurantListAdapter(new Restaurant[0], null, this::startRestaurantDetailActivity);

  public RestaurantListFragment() {}

  @Override
  public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentRestaurantsListBinding.inflate(inflater, container, false);
    initializeViewModel(container);
    binding.restaurantsList.setAdapter(listAdapter);
    binding.restaurantsList.setLayoutManager(new LinearLayoutManager(getContext()));
    currentLocationProvider.getCurrentCoordinates(
        currentLocation ->
            restaurantRelatedViewModel.getNearbyRestaurants(
                currentLocation, onNearbyRestaurantReceived(currentLocation)));
    return binding.getRoot();
  }

  @Override
  public void onPrepareOptionsMenu(@NonNull Menu menu) {
    menu.findItem(R.id.orderRestaurants).setVisible(true);
    menu.findItem(R.id.searchAction).setVisible(false);
    super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    final int selectedItemId = item.getItemId();
    Stream<Restaurant> sortedRestaurantStream;
    if (selectedItemId == R.id.orderByNearest) {
      sortedRestaurantStream = restaurants.stream().sorted(this::sortByDistance);
    } else if (selectedItemId == R.id.orderByStars) {
      sortedRestaurantStream = restaurants.stream().sorted(this::sortByStarsCount);
    } else if (selectedItemId == R.id.orderByNumberOfWorkmates) {
      sortedRestaurantStream = restaurants.stream().sorted(this::sortByWorkmatesCount);
    } else {
      return super.onOptionsItemSelected(item);
    }
    listAdapter.updateList(sortedRestaurantStream.toArray(Restaurant[]::new), currentLocation);
    return true;
  }


  public int sortByDistance(Restaurant restaurant1, Restaurant restaurant2) {
    final double restaurant1Distance =
        DistanceUtils.getDistanceBetween(restaurant1.getCoordinates(), currentLocation);
    final double restaurant2Distance =
        DistanceUtils.getDistanceBetween(restaurant2.getCoordinates(), currentLocation);
    return (int) (restaurant1Distance - restaurant2Distance);
  }

  public int sortByStarsCount(Restaurant restaurant1, Restaurant restaurant2) {
    return restaurant2.getStarsBoundedTo3() - restaurant1.getStarsBoundedTo3();
  }

  public int sortByWorkmatesCount(Restaurant restaurant1, Restaurant restaurant2) {
    Integer workmateCountInRestaurant1 = workmatesCountByRestaurant.get(restaurant1.getId());
    Integer workmateCountInRestaurant2 = workmatesCountByRestaurant.get(restaurant2.getId());
    if(workmateCountInRestaurant1 == null) workmateCountInRestaurant1 = 0;
    if(workmateCountInRestaurant2 == null) workmateCountInRestaurant2 = 0;
    return workmateCountInRestaurant2 - workmateCountInRestaurant1;
  }

  private Callback<Place[]> onNearbyRestaurantReceived(GeoCoordinates currentLocation) {
    this.currentLocation = currentLocation;
    return new Callback<Place[]>() {
      @Override
      public void onSuccess(Place[] places) {
        restaurants = Arrays.asList((Restaurant[]) places);
        listAdapter.updateList((Restaurant[]) places, currentLocation);
        if (binding.restaurantListShimmer.isShimmerVisible()) {
          binding.restaurantListShimmer.stopShimmer();
          binding.restaurantListShimmer.setVisibility(View.GONE);
        }
        fetchWorkmatesCountByRestaurant();
      }

      @Override
      public void onFailure() {}
    };
  }

  private void fetchWorkmatesCountByRestaurant() {
    restaurantRelatedViewModel.getWorkmatesCountByRestaurant(
        userProvider.getCurrentUser().getWorkplaceId(),
        new Callback<Map<String, Integer>>() {
          @Override
          public void onSuccess(Map<String, Integer> result) {
            workmatesCountByRestaurant = result;
            listAdapter.setWorkmatesCountByRestaurant(result);
          }

          @Override
          public void onFailure() {}
        });
  }

  private void startRestaurantDetailActivity(String restaurantId) {
    RestaurantDetailsActivity.navigate(restaurantId, getActivity());
  }

  private void initializeViewModel(View fragmentViewContainer) {
    final NavBackStackEntry backStackEntry =
        Navigation.findNavController(fragmentViewContainer)
            .getBackStackEntry(R.id.navigation_graph);
    restaurantRelatedViewModel =
        new ViewModelProvider(
                backStackEntry,
                HiltViewModelFactory.createInternal(getActivity(), backStackEntry, null, null))
            .get(RestaurantRelatedViewModel.class);
  }
}
