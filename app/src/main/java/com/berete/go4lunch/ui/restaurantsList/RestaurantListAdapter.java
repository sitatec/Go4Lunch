package com.berete.go4lunch.ui.restaurantsList;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.RestaurantListItemBinding;
import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.ui.core.utils.LocationUtils;
import com.bumptech.glide.Glide;

public class RestaurantListAdapter
    extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

  private Restaurant[] restaurants;
  private GeoCoordinates currentLocation;

  public RestaurantListAdapter(Restaurant[] restaurants, @Nullable GeoCoordinates currentLocation) {
    this.restaurants = restaurants;
    this.currentLocation = currentLocation;
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

  // ################## ------- INNER CLASSES ------- ################ //

  public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    private final RestaurantListItemBinding binding;

    public RestaurantViewHolder(RestaurantListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void updateView(Restaurant restaurant) {
      binding.restaurantName.setText(restaurant.getName());
      binding.restaurantAddress.setText(restaurant.getAddress());
      binding.restaurantOpeningHours.setText(restaurant.isOpen() ? "Open" : "Closed");
      Glide.with(binding.getRoot().getContext())
          .load(restaurant.getPhotoUrl())
          .placeholder(R.color.restaurant_placeholder_bg)
          .centerCrop()
          .into(binding.restaurantPhoto);
      String distanceAsString;
      if (currentLocation != null) {
        final Float distanceFromCurrentLocToRestaurant =
            LocationUtils.getDistanceBetween(restaurant.getCoordinates(), currentLocation);
        distanceAsString = Math.round(distanceFromCurrentLocToRestaurant) + "m";
      } else distanceAsString = "0.0m";
      binding.distanceFromCurrentLoc.setText(distanceAsString);
    }
  }
}
