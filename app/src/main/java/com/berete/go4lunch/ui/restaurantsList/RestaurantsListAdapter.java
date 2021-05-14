package com.berete.go4lunch.ui.restaurantsList;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.berete.go4lunch.databinding.FragmentRestaurantsListBinding;
import com.berete.go4lunch.ui.restaurantsList.models.Restaurant;

import java.util.List;

public class RestaurantsListAdapter extends RecyclerView.Adapter<RestaurantsListAdapter.ViewHolder> {

    private final List<Restaurant> mValues;

    public RestaurantsListAdapter(List<Restaurant> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentRestaurantsListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(FragmentRestaurantsListBinding binding) {
            super(binding.getRoot());
        }

    }
}