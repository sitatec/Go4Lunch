package com.berete.go4lunch.ui.restaurant.list;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.RestaurantListItemBinding;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.utils.DistanceUtils;
import com.berete.go4lunch.ui.core.adapters.ListAdapterCallback;
import com.berete.go4lunch.ui.restaurant.RestaurantUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Map;

public class RestaurantListAdapter
    extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

  private Restaurant[] restaurants;
  private GeoCoordinates currentLocation;
  private final ListAdapterCallback<String> onItemClicked;
  private Map<String, Integer> workmatesCountByRestaurant;

  public RestaurantListAdapter(
      Restaurant[] restaurants,
      @Nullable GeoCoordinates currentLocation,
      ListAdapterCallback<String> onItemClicked) {
    this.restaurants = restaurants;
    this.currentLocation = currentLocation;
    this.onItemClicked = onItemClicked;
  }

  @NonNull
  @Override
  public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final RestaurantListItemBinding binding =
        RestaurantListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new RestaurantViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
    holder.updateView(restaurants[position]);
  }

  @Override
  public int getItemCount() {
    return restaurants.length;
  }

  public void updateList(Restaurant[] restaurants, @Nullable GeoCoordinates currentLocation) {
    this.restaurants = restaurants;
    if (currentLocation != null) this.currentLocation = currentLocation;
    notifyDataSetChanged();
  }

  public void setWorkmatesCountByRestaurant(Map<String, Integer> workmatesCountByRestaurant) {
    this.workmatesCountByRestaurant = workmatesCountByRestaurant;
    notifyDataSetChanged();
  }

  // ################## ------- INNER CLASSES ------- ################ //

  public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    private final RestaurantListItemBinding binding;

    public RestaurantViewHolder(RestaurantListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void updateView(Restaurant restaurant) {
      binding.getRoot().setTag(restaurant); // helpful for the tests
      binding.restaurantName.setText(restaurant.getName());
      binding.restaurantAddress.setText(restaurant.getAddress());
      binding.distanceFromCurrentLoc.setText(getDistanceAsString(restaurant));
      setRestaurantStatus(restaurant);
      loadRestaurantPhoto(restaurant);
      binding.restaurantStars.setImageResource(
          RestaurantUtils.getStarsDrawableId(restaurant.getStarsBoundedTo3()));
      binding.getRoot().setOnClickListener(v -> onItemClicked.call(restaurant.getId()));
      setWorkmatesCount(restaurant);
    }

    private void setWorkmatesCount(Restaurant restaurant) {
      if (workmatesCountByRestaurant != null) {
        final Integer workmatesCount = workmatesCountByRestaurant.get(restaurant.getId());
        if (workmatesCount != null && workmatesCount != 0) {
          final String formattedWorkmateCount =
              binding.getRoot().getResources().getString(R.string.in_brackets, workmatesCount);
          binding.numberOfWorkmatesThere.setText(formattedWorkmateCount);
        }
      }
    }

    private String getDistanceAsString(Restaurant restaurant) {
      if (currentLocation == null) return "Err";
      return DistanceUtils.getDisplayableDistance(currentLocation, restaurant.getCoordinates());
    }

    public void setRestaurantStatus(Restaurant restaurant) {
      if (restaurant.isOpen()) {
        binding.restaurantOpeningHours.setText(R.string.restaurant_status_open);
        binding.restaurantOpeningHours.setTextColor(getSupportColor(R.color.light_green));
      } else {
        binding.restaurantOpeningHours.setText(R.string.restaurant_status_closed);
        binding.restaurantOpeningHours.setTextColor(getSupportColor(R.color.light_red));
      }
    }

    private int getSupportColor(@ColorRes int colorId) {
      return ResourcesCompat.getColor(binding.getRoot().getResources(), colorId, null);
    }

    private void loadRestaurantPhoto(Restaurant restaurant) {
      Glide.with(binding.getRoot().getContext())
          .load(restaurant.getMainPhotoUrl())
          .centerCrop()
          .listener(getPhotoLoadedListener())
          .into(binding.restaurantPhoto);
    }

    private RequestListener<Drawable> getPhotoLoadedListener() {
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
          return false;
        }
      };
    }
  }
}
