package com.berete.go4lunch.ui.workmates;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.berete.go4lunch.databinding.FragmentWorkmatesBinding;
import com.berete.go4lunch.ui.workmates.models.Workmate;

import java.util.List;

public class WorkmatesListAdapter extends RecyclerView.Adapter<WorkmatesListAdapter.ViewHolder> {

    private final List<Workmate> mValues;

    public WorkmatesListAdapter(List<Workmate> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentWorkmatesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(FragmentWorkmatesBinding binding) {
            super(binding.getRoot());
        }

    }
}