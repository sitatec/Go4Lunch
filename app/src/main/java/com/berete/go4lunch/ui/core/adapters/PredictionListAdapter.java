package com.berete.go4lunch.ui.core.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berete.go4lunch.databinding.PredictionListItemBinding;
import com.berete.go4lunch.domain.restaurants.models.Prediction;

public class PredictionListAdapter
    extends RecyclerView.Adapter<PredictionListAdapter.PredictionViewHolder> {

  private Prediction[] predictions = new Prediction[0];
  private final ListAdapterCallback<String> onItemSelectedListener;

  public PredictionListAdapter(ListAdapterCallback<String> onItemSelectedListener) {
    this.onItemSelectedListener = onItemSelectedListener;
  }

  @NonNull
  @Override
  public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final PredictionListItemBinding predictionListItemBinding =
        PredictionListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new PredictionViewHolder(predictionListItemBinding);
  }

  @Override
  public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
    holder.updateView(predictions[position]);
  }

  @Override
  public int getItemCount() {
    return predictions.length;
  }

  public void updateList(Prediction[] predictions) {
    this.predictions = predictions;
    notifyDataSetChanged();
  }

  // ################## ------- INNER CLASSES ------- ################ //

  public class PredictionViewHolder extends RecyclerView.ViewHolder {

    private final PredictionListItemBinding binding;

    public PredictionViewHolder(PredictionListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void updateView(Prediction prediction) {
      binding
          .getRoot()
          .setOnClickListener(
              v -> onItemSelectedListener.call(prediction.getCorrespondingPlaceId()));
      binding.bestMatch.setText(prediction.getBestMatch());
      binding.relatedText.setText(prediction.getRelatedText());
      binding.distanceFromCurrentLoc.setText(prediction.getDisplayableDistance());
    }
  }

}
