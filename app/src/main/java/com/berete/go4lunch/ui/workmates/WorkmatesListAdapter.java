package com.berete.go4lunch.ui.workmates;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.WorkmateListItemBinding;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.ui.core.adapters.ListAdapterCallback;
import com.bumptech.glide.Glide;

public class WorkmatesListAdapter
    extends RecyclerView.Adapter<WorkmatesListAdapter.WorkmateViewHolder> {

  private User[] workmates = new User[0];
  private final ListAdapterCallback<String> onWorkmatesSelected;

  public WorkmatesListAdapter(ListAdapterCallback<String> onWorkmatesSelected) {
    this.onWorkmatesSelected = onWorkmatesSelected;
  }

  @Override
  public WorkmateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final WorkmateListItemBinding binding =
        WorkmateListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new WorkmateViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(final WorkmateViewHolder holder, int position) {
    holder.updateView(workmates[position]);
  }

  @Override
  public int getItemCount() {
    return workmates.length;
  }

  public void updateList(User[] newWorkmates){
    workmates = newWorkmates;
    notifyDataSetChanged();
  }

  public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    final WorkmateListItemBinding binding;

    public WorkmateViewHolder(WorkmateListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void updateView(User workmate) {
      binding.choosedRestaurantName.setText(workmate.getChosenRestaurantName());
      String itemTitle;
      final Resources resources = binding.getRoot().getResources();
      if (workmate.getChosenRestaurantId() == null || workmate.getChosenRestaurantId().isEmpty()) {
        itemTitle = resources.getString(R.string.has_not_decided_yet_txt, workmate.getUsername());
        binding.choosedRestaurantName.setVisibility(View.GONE);
      } else {
        itemTitle = resources.getString(R.string.is_eating_at_txt, workmate.getUsername());
        binding.getRoot().setOnClickListener(v -> onWorkmatesSelected.call(workmate.getChosenRestaurantId()));
      }
      binding.itemTitle.setText(itemTitle);
      Glide.with(binding.getRoot()).load(workmate.getPhotoUrl()).centerCrop().into(binding.workmatesPhoto);
    }
  }
}
