package com.berete.go4lunch.ui.restaurant.details;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.ui.restaurant.RestaurantUtils;
import com.bumptech.glide.Glide;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantDetailsActivity extends AppCompatActivity {

  ActivityRestaurantDetailsBinding binding;
  private RestaurantDetailViewModel viewModel;
  private Restaurant restaurant;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    viewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
    final int toolbarTitleColor = getResources().getColor(R.color.white);
    binding.collapsingToolbarLayout.setExpandedTitleColor(toolbarTitleColor);
    binding.collapsingToolbarLayout.setCollapsedTitleTextColor(toolbarTitleColor);
    binding.collapsingToolbarLayout.setExpandedTitleMarginStart(36);
    binding.collapsingToolbarLayout.setForegroundGravity(
        Gravity.RIGHT | Gravity.BOTTOM | Gravity.CENTER);
    binding.toolbar.setTitleTextColor(toolbarTitleColor);
    getRestaurantDetails();
  }

  private void getRestaurantDetails() {
    final String restaurantId = getIntent().getStringExtra("restaurantId");
    viewModel.getRestaurantDetails(
        restaurantId,
        new Callback<Place>() {
          @Override
          public void onSuccess(Place place) {
            Log.i("REQUEST_RESULT_SUCCESS", place.toString());
            restaurant = new Restaurant(place);
            setViewsData();
          }

          @Override
          public void onFailure() {
            Log.i("REQUEST_RESULT", "____FAILURE____");
          }
        });
  }

  private void setViewsData() {
    binding.restaurantStars.setImageResource(
        RestaurantUtils.getStarsDrawableId(restaurant.getStarsBoundedTo3()));
    binding.collapsingToolbarLayout.setTitle(restaurant.getName());
    binding.restaurantAddress.setText(restaurant.getAddress());
    Glide.with(this).load(restaurant.getMainPhotoUrl()).centerCrop().into(binding.restaurantPhoto);
    setUpActionListeners();
  }

  private void setUpActionListeners() {
    binding.callButton.setOnClickListener(this::handleTheCallAction);
    binding.urlButton.setOnClickListener(this::handleLaunchingTheWebsite);
  }

  private void handleTheCallAction(View callAction) {
    if (restaurant.getPhoneNumber() == null) {
      callAction.setEnabled(false);
    } else {
      final Uri phoneUri = Uri.parse("tel:" + restaurant.getPhoneNumber());
      startActivity(new Intent(Intent.ACTION_VIEW, phoneUri));
    }
  }

  private void handleLaunchingTheWebsite(View openWebsiteAction) {
    if (restaurant.getPhoneNumber() == null) {
      openWebsiteAction.setEnabled(false);
    } else {
      final Uri websiteUri = Uri.parse(restaurant.getWebsiteUrl());
      startActivity(new Intent(Intent.ACTION_VIEW, websiteUri));
    }
  }

  public static void start(String restaurantId, Activity sourceActivity) {
    final Bundle arg = new Bundle();
    arg.putString("restaurantId", restaurantId);
    Navigation.findNavController(sourceActivity, R.id.fragmentContainerView)
        .navigate(R.id.restaurantDetailsActivity, arg);
  }
}
