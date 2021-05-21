package com.berete.go4lunch.ui.core.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityMainBinding;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.domain.restaurants.services.CurrentLocationProvider;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.ui.core.adapters.PredictionListAdapter;
import com.berete.go4lunch.ui.core.view_models.MainActivityViewModel;
import com.berete.go4lunch.ui.restaurant.details.RestaurantDetailsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  @Inject public CurrentLocationProvider currentLocationProvider;

  private ActivityMainBinding binding;
  private MainActivityViewModel viewModel;
  private GeoCoordinates currentLocation;
  private final PredictionListAdapter predictionListAdapter =
      new PredictionListAdapter(this::displayRestaurantDetail);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setupNavigation();
  }

  private void setupNavigation() {
    final NavHostFragment navHostFragment =
        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
    assert navHostFragment != null;
    final NavController navController = navHostFragment.getNavController();
    final Toolbar toolbar = findViewById(R.id.toolbar);
    final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
    final NavigationView navigationView = findViewById(R.id.bottom_navigation_view);
    setSupportActionBar(toolbar);
    NavigationUI.setupActionBarWithNavController(this, navController, getAppBarConfig());
    NavigationUI.setupWithNavController(navigationView, navController);
    NavigationUI.setupWithNavController(bottomNavigationView, navController);
  }

  private AppBarConfiguration getAppBarConfig() {
    return new AppBarConfiguration.Builder(
            R.id.mapFragment,
            R.id.restaurantListFragment,
            R.id.workmatesListFragment,
            R.id.conversationsListFragment)
        .setOpenableLayout(binding.getRoot())
        .build();
  }

  @Override
  public boolean onSupportNavigateUp() {
    final NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);;
    return NavigationUI.navigateUp(navController, getAppBarConfig())
        || super.onSupportNavigateUp();
  }

  private void displayRestaurantDetail(String restaurantId) {
    RestaurantDetailsActivity.start(restaurantId, this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_menu, menu);
    final SearchView searchField = (SearchView) menu.findItem(R.id.searchAction).getActionView();
    searchField.setOnQueryTextFocusChangeListener(this::onSearchFieldFocusChanged);
    searchField.setOnQueryTextListener(getQueryListener());
    searchField.setQueryHint(getString(R.string.search_restaurants_hint));
    return super.onCreateOptionsMenu(menu);
  }

  private void onSearchFieldFocusChanged(View v, boolean hasFocus) {
    if (hasFocus) {
      startSearchSession();
    } else {
      endSearchSession();
    }
  }

  private void startSearchSession() {
    currentLocationProvider.getCurrentCoordinates(
        new CurrentLocationProvider.OnCoordinatesResultListener() {
          @Override
          public void onResult(GeoCoordinates geoCoordinates) {
            currentLocation = geoCoordinates;
          }
        });
    binding.predictionList.setLayoutManager(new LinearLayoutManager(this));
    binding.predictionList.setAdapter(predictionListAdapter);
    binding.searchView.setVisibility(View.VISIBLE);
    initViewModel();
  }

  private void endSearchSession() {
    binding.searchView.setVisibility(View.GONE);
  }

  private void initViewModel() {
    viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    viewModel.setPredictionResultListener(
        new Callback<Prediction[]>() {
          @Override
          public void onSuccess(Prediction[] predictions) {
            predictionListAdapter.updateList(predictions);
          }

          @Override
          public void onFailure() {}
        });
  }

  // When the user start typing the restaurant name, we will not send an autocomplete request for
  // each character (for the billing purpose) instead, we will send a request for each character
  // whose position in the word is impair, ie: the sequence for the Go4Lunch word would be
  // "G", "Go4", "Go4Lu", "Go4Lunc". Then if the user type "Go4Lunch" and  last autocompleted word
  // was "Go4Lunc", he will submit a search query for the complete word.
  private SearchView.OnQueryTextListener getQueryListener() {
    return new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (!isStringLengthPair(query) && currentLocation != null) viewModel.getPredictions(query, currentLocation);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (isStringLengthPair(newText) && currentLocation != null) viewModel.getPredictions(newText, currentLocation);
        return false;
      }
    };
  }

  private boolean isStringLengthPair(String stringToCheck) {
    return stringToCheck.length() % 2 != 0;
  }
}
