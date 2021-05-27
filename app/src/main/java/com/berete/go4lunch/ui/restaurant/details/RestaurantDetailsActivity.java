package com.berete.go4lunch.ui.restaurant.details;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.ui.restaurant.RestaurantUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantDetailsActivity extends AppCompatActivity {

  ActivityRestaurantDetailsBinding binding;
  private RestaurantDetailViewModel viewModel;
  private Restaurant restaurant;

  @Inject UserProvider userProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    stylizeTheCollapsingToolbar();
    viewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
    getRestaurantDetails();
  }

  private void stylizeTheCollapsingToolbar() {
    final int toolbarTitleColor = getResources().getColor(R.color.white);
    binding.collapsingToolbarLayout.setExpandedTitleColor(toolbarTitleColor);
    binding.collapsingToolbarLayout.setCollapsedTitleTextColor(toolbarTitleColor);
    binding.collapsingToolbarLayout.setExpandedTitleMarginStart(36);
    binding.collapsingToolbarLayout.setForegroundGravity(
        Gravity.END | Gravity.BOTTOM | Gravity.CENTER);
    // binding.toolbar.setTitleTextColor(toolbarTitleColor);

  }

  private void getRestaurantDetails() {
    final String restaurantId = getIntent().getStringExtra("restaurantId");
    viewModel.getRestaurantDetails(
        restaurantId,
        new Callback<Place>() {
          @Override
          public void onSuccess(Place place) {
            restaurant = new Restaurant(place);
            setViewsData();
          }

          @Override
          public void onFailure() {
            // TODO handle errors
          }
        });
  }


  private void setViewsData() {
    binding.restaurantStars.setImageResource(
        RestaurantUtils.getStarsDrawableId(restaurant.getStarsBoundedTo3()));
    binding.collapsingToolbarLayout.setTitle(restaurant.getName());
    binding.restaurantAddress.setText(restaurant.getAddress());
    updateRestaurantChoiceButton();
    updateRestaurantLikeButton();
    Glide.with(this)
        .load(restaurant.getMainPhotoUrl())
        .centerCrop()
        .listener(getOnImageLoadListener())
        .into(binding.restaurantPhoto);
    setUpActionListeners();
    setUpClientList();
  }

  private void updateRestaurantChoiceButton() {
    if (userProvider.getCurrentUser().getChosenRestaurantId().equals(restaurant.getId())) {
      binding.actionChooseRestaurant.setImageResource(R.drawable.ic_checked_box_24);
    } else {
      binding.actionChooseRestaurant.setImageResource(R.drawable.ic_indeterminate_check_box_24);
    }
  }

  private void updateRestaurantLikeButton(){
    final Drawable starIcon;
    if(userProvider.getCurrentUser().getLikedRestaurantsIds().contains(restaurant.getId())){
      starIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_one_star_24, null);
    } else {
      starIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_border_24, null);
    }
    starIcon.setBounds(binding.likeButton.getCompoundDrawables()[1].getBounds());
    binding.likeButton.setCompoundDrawables(null, starIcon, null, null);
  }

  private RequestListener<Drawable> getOnImageLoadListener() {
    return new RequestListener<Drawable>() {
      @Override
      public boolean onLoadFailed(
          @Nullable GlideException e,
          Object model,
          Target<Drawable> target,
          boolean isFirstResource) {
        return false;
      }

      @Override
      public boolean onResourceReady(
          Drawable resource,
          Object model,
          Target<Drawable> target,
          DataSource dataSource,
          boolean isFirstResource) {
        binding.restaurantPhotoShimmer.stopShimmer();
        binding.restaurantPhotoShimmer.setVisibility(View.GONE);
        binding.shadow.setVisibility(View.VISIBLE);
        return false;
      }
    };
  }

  private void setUpActionListeners() {
    binding.callButton.setOnClickListener(this::handleTheCallAction);
    binding.urlButton.setOnClickListener(this::launchTheRestaurantWebsite);
    binding.likeButton.setOnClickListener(this::toggleLikeButtonState);
    binding.actionChooseRestaurant.setOnClickListener(this::toggleRestaurantChoiceButton);
  }

  private void handleTheCallAction(View callAction) {
    if (restaurant.getPhoneNumber() == null) {
      callAction.setEnabled(false);
    } else {
      final Uri phoneUri = Uri.parse("tel:" + restaurant.getPhoneNumber());
      startActivity(new Intent(Intent.ACTION_VIEW, phoneUri));
    }
  }

  private void launchTheRestaurantWebsite(View openWebsiteAction) {
    if (restaurant.getPhoneNumber() == null) {
      openWebsiteAction.setEnabled(false);
    } else {
      final Uri websiteUri = Uri.parse(restaurant.getWebsiteUrl());
      startActivity(new Intent(Intent.ACTION_VIEW, websiteUri));
    }
  }

  private void toggleLikeButtonState(View likeButton) {
    final List<String> currentUserLikedRestaurants =
        userProvider.getCurrentUser().getLikedRestaurantsIds();
    if (currentUserLikedRestaurants.contains(restaurant.getId())) {
      currentUserLikedRestaurants.remove(restaurant.getId());
    } else {
      currentUserLikedRestaurants.add(restaurant.getId());
    }
    userProvider.updateUserData(UserProvider.LIKED_RESTAURANTS, currentUserLikedRestaurants);
    updateRestaurantLikeButton();
  }

  private void toggleRestaurantChoiceButton(View restaurantChoiceButton) {
    final User currentUser = userProvider.getCurrentUser();
    if (currentUser.getChosenRestaurantId().equals(restaurant.getId())) {
      userProvider.resetCurrentUserChosenRestaurant();
      binding.actionChooseRestaurant.setImageResource(R.drawable.ic_indeterminate_check_box_24);
    } else {
      currentUser.setChosenRestaurantId(restaurant.getId());
      currentUser.setChosenRestaurantName(restaurant.getName());
      binding.actionChooseRestaurant.setImageResource(R.drawable.ic_checked_box_24);
      userProvider.updateUserData(UserProvider.CHOSEN_RESTAURANT_ID, currentUser.getChosenRestaurantId());
    }
  }

  private void setUpClientList() {
    final String currentUserWorkplaceId = userProvider.getCurrentUser().getWorkplaceId();
    if (currentUserWorkplaceId == null || currentUserWorkplaceId.isEmpty()) {
      binding.workplaceRequiredMessage.setVisibility(View.VISIBLE);
      binding.selectMyWorkplaceAction.setOnClickListener(__ -> finish()); // Go back to the
      // MainActivity which will show the WorkplacePickerDialog
    } else
      viewModel.getWorkmatesByChosenRestaurant(
          restaurant.getId(),
          new Callback<User[]>() {
            @Override
            public void onSuccess(User[] users) {
              if (users.length == 0) showWorkmatesDataUnavailableMessage();
              binding.workmatesWhoGoToThisRestaurant.setLayoutManager(
                  new LinearLayoutManager(RestaurantDetailsActivity.this));
              binding.workmatesWhoGoToThisRestaurant.setAdapter(
                  new RestaurantClientListAdapter(users));
            }

            @Override
            public void onFailure() {
              // TODO handle errors
            }
          });
  }


  private void showWorkmatesDataUnavailableMessage() {}

  public static void navigate(String restaurantId, Activity sourceActivity) {
    final Bundle arg = new Bundle();
    arg.putString("restaurantId", restaurantId);
    final Intent intent = new Intent(sourceActivity, RestaurantDetailsActivity.class);
    intent.putExtras(arg);
    sourceActivity.startActivity(intent);
  }
}
