package com.berete.go4lunch.ui.core.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityMainBinding;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.domain.restaurants.services.CurrentLocationProvider;
import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.ui.core.adapters.PredictionListAdapter;
import com.berete.go4lunch.ui.core.fragments.WorkplacePickerDialogFragment;
import com.berete.go4lunch.ui.core.notification.LunchAlarmManager;
import com.berete.go4lunch.ui.core.notification.LunchAlarmReceiver;
import com.berete.go4lunch.ui.core.services.location.LocationPermissionHandler;
import com.berete.go4lunch.ui.core.view_models.MainActivityViewModel;
import com.berete.go4lunch.ui.restaurant.details.RestaurantDetailsActivity;
import com.berete.go4lunch.ui.settings.SettingsUtils;
import com.berete.go4lunch.ui.settings.TimePreference;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import pub.devrel.easypermissions.EasyPermissions;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  @Inject public CurrentLocationProvider currentLocationProvider;
  @Inject public UserProvider userProvider;
  @Inject public LocationPermissionHandler locationPermissionHandler;

  private final Set<WeakReference<OnWorkplaceSelected>> onWorkplaceSelectedListeners =
      new HashSet<>();
  private ActivityMainBinding binding;
  private MainActivityViewModel viewModel;
  private GeoCoordinates currentLocation;
  private static WorkplacePickerDialogFragment workplacePicker;
  private final PredictionListAdapter predictionListAdapter =
      new PredictionListAdapter(this::displayRestaurantDetail);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setupNavigation();
    initViewModel();
    userProvider.addAuthStateChangesListener(this::onAuthStateChanges);
    scheduleAlarmIfNeeded();
  }

  private void scheduleAlarmIfNeeded() {
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    final boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);
    if (isFirstLaunch) {
      Calendar defaultLunchTime = new TimePreference(this).getPersistedTimeAsCalendar();
      if (defaultLunchTime.before(Calendar.getInstance())) {
        // The alarm time must not be in the past otherwise the alarm manager will trigger
        // immediately
        defaultLunchTime.add(Calendar.DAY_OF_MONTH, 1);
      }
      new LunchAlarmManager(this).scheduleAlarm(defaultLunchTime, LunchAlarmReceiver.class);
      sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply();
    }
  }

  private void onAuthStateChanges(User currentUser) {
    if (currentUser == null) {
      throw new IllegalStateException();
    } else {
      setNavDrawerHeaderData(currentUser);
      if (currentUser.getWorkplaceId() == null || currentUser.getWorkplaceId().isEmpty()) {
        showWorkplacePikerInternal();
      }
    }
  }

  private void setupNavigation() {
    final NavHostFragment navHostFragment =
        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
    assert navHostFragment != null;
    final NavController navController = navHostFragment.getNavController();
    setSupportActionBar(binding.toolbar);
    NavigationUI.setupActionBarWithNavController(this, navController, getAppBarConfig());
    NavigationUI.setupWithNavController(binding.navigationView, navController);
    binding.navigationView.setNavigationItemSelectedListener(
        this::onNavigationDrawerMenuItemSelected);
    NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
  }

  private void setNavDrawerHeaderData(User currentUser) {
    final View drawerHeader = binding.navigationView.getHeaderView(0);
    ((TextView) drawerHeader.findViewById(R.id.username)).setText(currentUser.getUsername());
    ((TextView) drawerHeader.findViewById(R.id.user_email)).setText(currentUser.getEmail());
    final ImageView userPhoto = drawerHeader.findViewById(R.id.user_photo);
    Glide.with(this).load(currentUser.getPhotoUrl()).centerCrop().into(userPhoto);
  }

  private boolean onNavigationDrawerMenuItemSelected(MenuItem menuItem) {
    if (menuItem.getItemId() == R.id.logout) {
      signOut();
    } else if (menuItem.getItemId() == R.id.user_lunch) {
      showCurrentUserChosenRestaurant();
    } else if (menuItem.getItemId() == R.id.settingsFragment) {
      Navigation.findNavController(binding.fragmentContainerView).navigate(R.id.settingsFragment);
      binding.getRoot().closeDrawer(binding.navigationView, false);
    }
    return true;
  }

  private void showCurrentUserChosenRestaurant() {
    final String currentUserChosenRestaurant =
        userProvider.getCurrentUser().getChosenRestaurantId();
    if (currentUserChosenRestaurant != null && !currentUserChosenRestaurant.isEmpty()) {
      RestaurantDetailsActivity.navigate(currentUserChosenRestaurant, this);
    } else
      Snackbar.make(binding.getRoot(), R.string.restaurant_not_chosen_yet, Snackbar.LENGTH_SHORT)
          .show();
  }

  private void signOut() {
    AuthUI.getInstance()
        .signOut(this)
        .addOnSuccessListener(
            __ -> {
              startActivity(new Intent(this, EntryPointActivity.class));
              finish();
            });
  }

  private void showWorkplacePikerInternal() {
    if (workplacePicker == null) {
      workplacePicker =
          new WorkplacePickerDialogFragment(
              this::onWorkplacePredictionSelected,
              newText -> viewModel.getWorkplacePredictions(newText));
    }
    workplacePicker.show(getSupportFragmentManager(), getString(R.string.workplace_picker_tag));
  }

  private void onWorkplacePredictionSelected(Prediction selectedPrediction) {
    final String selectedWorkplaceId = selectedPrediction.getCorrespondingPlaceId();
    userProvider.getCurrentUser().setWorkplaceId(selectedWorkplaceId);
    workplacePicker.dismiss();
    userProvider.updateUserData(UserProvider.WORKPLACE, selectedWorkplaceId);
    for (WeakReference<OnWorkplaceSelected> listener : onWorkplaceSelectedListeners) {
      if (listener.get() != null) listener.get().onSelected(selectedPrediction);
    }
    updateUserPreferences(selectedPrediction);
    Snackbar.make(
            binding.getRoot(),
            getString(R.string.workplace_selection_success_msg, selectedPrediction.getBestMatch()),
            Snackbar.LENGTH_LONG)
        .show();
  }

  private void updateUserPreferences(Prediction selectedWorkplacePrediction) {
    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    final SharedPreferences.Editor preferencesEditor = preferences.edit();
    preferencesEditor.putString(
        "workplace", SettingsUtils.toFormattedWorkplaceSummary(selectedWorkplacePrediction));
    preferencesEditor.apply();
  }

  public void showWorkplacePiker(OnWorkplaceSelected onWorkplaceSelected) {
    onWorkplaceSelectedListeners.add(new WeakReference<>(onWorkplaceSelected));
    showWorkplacePikerInternal();
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
    final NavController navController =
        Navigation.findNavController(this, R.id.fragmentContainerView);
    return NavigationUI.navigateUp(navController, getAppBarConfig()) || super.onSupportNavigateUp();
  }

  private void displayRestaurantDetail(String restaurantId) {
    RestaurantDetailsActivity.navigate(restaurantId, this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_menu, menu);
    menu.findItem(R.id.orderRestaurants).setVisible(false);
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
    currentLocationProvider.getCurrentCoordinates(currentLoc -> currentLocation = currentLoc);
    binding.predictionList.setLayoutManager(new LinearLayoutManager(this));
    binding.predictionList.setAdapter(predictionListAdapter);
    binding.searchView.setVisibility(View.VISIBLE);
  }

  private void endSearchSession() {
    binding.searchView.setVisibility(View.GONE);
  }

  private void initViewModel() {
    viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    viewModel.setRestaurantPredictionResultListener(
        new Callback<Prediction[]>() {
          @Override
          public void onSuccess(Prediction[] predictions) {
            predictionListAdapter.updateList(predictions);
          }

          @Override
          public void onFailure() {}
        });

    viewModel.setWorkplacePredictionListener(
        new Callback<Prediction[]>() {
          @Override
          public void onSuccess(Prediction[] predictions) {
            workplacePicker.updatePredictionsList(predictions);
          }

          @Override
          public void onFailure() {}
        });
  }

  private SearchView.OnQueryTextListener getQueryListener() {
    return new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        final InputMethodManager inputMethodManager =
            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (currentLocation != null) {
          viewModel.getRestaurantPrediction(newText, currentLocation);
        }
        return false;
      }
    };
  }

  public interface OnWorkplaceSelected {
    void onSelected(Prediction workplacePrediction);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(
        requestCode, permissions, grantResults, locationPermissionHandler);
  }
}
