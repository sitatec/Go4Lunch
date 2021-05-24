package com.berete.go4lunch.ui.core.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berete.go4lunch.databinding.WorkplacePredictionListItemBinding;
import com.berete.go4lunch.domain.restaurants.models.Prediction;

public class WorkplacePredictionsListAdapter extends RecyclerView.Adapter<WorkplacePredictionsListAdapter.WorkplacePredictionViewHolder> {

  Prediction[] predictedWorkplaces = new Prediction[0];
  final ListAdapterCallback<Prediction> onWorkplaceSelected;

  public WorkplacePredictionsListAdapter(ListAdapterCallback<Prediction> onWorkplaceSelected){
    this.onWorkplaceSelected = onWorkplaceSelected;
  }

  @NonNull
  @Override
  public WorkplacePredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final WorkplacePredictionListItemBinding binding =
        WorkplacePredictionListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new WorkplacePredictionViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull WorkplacePredictionViewHolder holder, int position) {
    holder.updateView(predictedWorkplaces[position]);
  }

  @Override
  public int getItemCount() {
    return predictedWorkplaces.length;
  }

  public void updateList(Prediction[] newPredictions){
    predictedWorkplaces = newPredictions;
    notifyDataSetChanged();
  }

  public class WorkplacePredictionViewHolder extends RecyclerView.ViewHolder {

    final private WorkplacePredictionListItemBinding binding;

    public WorkplacePredictionViewHolder(WorkplacePredictionListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void updateView(Prediction predictedWorkplace){
      binding.workplaceName.setText(predictedWorkplace.getBestMatch());
      binding.workplaceAddress.setText(predictedWorkplace.getRelatedText());
      binding.getRoot().setOnClickListener(v -> onWorkplaceSelected.call(predictedWorkplace));
    }

  }
}
