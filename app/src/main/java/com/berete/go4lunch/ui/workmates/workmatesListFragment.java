package com.berete.go4lunch.ui.workmates;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.FragmentWorkmatesBinding;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.ui.core.activities.MainActivity;
import com.berete.go4lunch.ui.core.view_models.shared.UserRelatedViewModel;
import com.berete.go4lunch.ui.restaurant.details.RestaurantDetailsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;

@AndroidEntryPoint
public class workmatesListFragment extends Fragment {

  private UserRelatedViewModel viewModel;
  private User currentUser;
  final WorkmatesListAdapter workmatesListAdapter =
      new WorkmatesListAdapter(
          restaurantId -> RestaurantDetailsActivity.navigate(restaurantId, getActivity()));

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(
      @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final FragmentWorkmatesBinding binding =
        FragmentWorkmatesBinding.inflate(
            LayoutInflater.from(container.getContext()), container, false);
    binding.workmatesList.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    binding.workmatesList.setAdapter(workmatesListAdapter);
    intiViewMode(container);
    currentUser = viewModel.getCurrentUser().getValue();
    viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> currentUser = user);
    handleWorkplaceRequiredMessageVisibility(binding);
    viewModel.getCurrentUserWorkmates(getWorkmatesRequestCallback());
    return binding.getRoot();
  }

  @Override
  public void onPrepareOptionsMenu(@NonNull Menu menu) {
    menu.findItem(R.id.searchAction).setVisible(false);
    super.onPrepareOptionsMenu(menu);
  }

  private void handleWorkplaceRequiredMessageVisibility(FragmentWorkmatesBinding binding) {
    if (currentUser.getWorkplaceId() == null || currentUser.getWorkplaceId().isEmpty()) {
      binding.workplaceRequiredMessage.setVisibility(View.VISIBLE);
      binding.selectMyWorkplaceAction.setOnClickListener(
          v -> {
            ((MainActivity) getActivity())
                .showWorkplacePiker(
                    __ -> binding.workplaceRequiredMessage.setVisibility(View.GONE));
          });
    }
  }

  private void intiViewMode(View view) {
    final NavBackStackEntry backStackEntry =
        Navigation.findNavController(view).getCurrentBackStackEntry();
    viewModel =
        new ViewModelProvider(
                backStackEntry,
                HiltViewModelFactory.createInternal(getActivity(), backStackEntry, null, null))
            .get(UserRelatedViewModel.class);
  }

  private Callback<User[]> getWorkmatesRequestCallback() {
    return new Callback<User[]>() {
      @Override
      public void onSuccess(User[] users) {
        Log.d("WORKMATES_RESPONSE", "____SUCCESS____ : " + Arrays.toString(users));
        workmatesListAdapter.updateList(users);
      }

      @Override
      public void onFailure() {
        Log.d("WORKMATES_RESPONSE", "____FAILED____");
        // TODO implement
      }
    };
  }
}
