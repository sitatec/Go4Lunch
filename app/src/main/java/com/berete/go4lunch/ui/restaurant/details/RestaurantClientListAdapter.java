package com.berete.go4lunch.ui.restaurant.details;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.RestaurantClientItemBinding;
import com.berete.go4lunch.domain.shared.models.User;
import com.bumptech.glide.Glide;

public class RestaurantClientListAdapter
    extends RecyclerView.Adapter<RestaurantClientListAdapter.ClientViewHolder> {

  private final User[] restaurantClients;

  public RestaurantClientListAdapter(User[] restaurantClients) {
    this.restaurantClients = restaurantClients;
  }

  @NonNull
  @Override
  public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final RestaurantClientItemBinding itemBinding =
        RestaurantClientItemBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
    return new ClientViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
    holder.updateView(restaurantClients[position]);
  }

  @Override
  public int getItemCount() {
    return restaurantClients.length;
  }

  // ##########  INNER_CLASSES  ######### //

  public static class ClientViewHolder extends RecyclerView.ViewHolder {

    final RestaurantClientItemBinding binding;

    public ClientViewHolder(@NonNull RestaurantClientItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void updateView(User user) {
      final Resources resources = binding.getRoot().getResources();
      binding.itemTitle.setText(
          resources.getString(R.string.workmate_is_joining, user.getUsername()));
      Glide.with(binding.getRoot())
          .load(user.getPhotoUrl())
          .centerCrop()
          .into(binding.workmatesPhoto);
    }
  }
}
