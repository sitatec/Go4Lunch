package com.berete.go4lunch.ui.core.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.WorkplacePickerDialogBinding;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.ui.core.adapters.ListAdapterCallback;
import com.berete.go4lunch.ui.core.adapters.WorkplacePredictionsListAdapter;

public class WorkplacePickerDialogFragment extends DialogFragment {

  final ListAdapterCallback<Prediction> onWorkplaceSelected;
  final ListAdapterCallback<String> onSearchFieldTextChange;
  final WorkplacePredictionsListAdapter predictionsListAdapter;

  public WorkplacePickerDialogFragment(
      ListAdapterCallback<Prediction> onPredictionSelected,
      ListAdapterCallback<String> onSearchFieldTextChange) {
    this.onWorkplaceSelected = onPredictionSelected;
    this.onSearchFieldTextChange = onSearchFieldTextChange;
    predictionsListAdapter = new WorkplacePredictionsListAdapter(onPredictionSelected);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    final WorkplacePickerDialogBinding binding =
        WorkplacePickerDialogBinding.inflate(getLayoutInflater());
    binding.workplacePredictionsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    binding.workplacePredictionsList.setAdapter(predictionsListAdapter);
    binding.workplaceSearchField.addTextChangedListener(getSearchFieldTextWatcher());
    return new AlertDialog.Builder(getActivity())
        .setView(binding.getRoot())
        .setTitle(R.string.workplace_picker_title)
        .setNegativeButton(R.string.not_now_txt, (dialog, ___) -> dialog.cancel())
        .create();
  }

  private TextWatcher getSearchFieldTextWatcher(){
    return new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        onSearchFieldTextChange.call(s.toString());
      }

      @Override public void afterTextChanged(Editable s) {}
    };
  }

  public void updatePredictionsList(Prediction[] newPredictions) {
    Log.d("WORKPLACE_PREDICTION", "NEW PREDICTION RESULT COUNT : " + newPredictions.length);
    predictionsListAdapter.updateList(newPredictions);
  }
}
