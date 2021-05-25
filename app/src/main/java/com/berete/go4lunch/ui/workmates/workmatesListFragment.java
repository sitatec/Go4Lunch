package com.berete.go4lunch.ui.workmates;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.berete.go4lunch.databinding.FragmentWorkmatesBinding;
import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.ui.core.activities.MainActivity;
import com.berete.go4lunch.ui.core.view_models.shared.UserRelatedViewModel;
import com.berete.go4lunch.ui.restaurant.details.RestaurantDetailsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;

@AndroidEntryPoint
public class workmatesListFragment extends Fragment {

  UserRelatedViewModel viewModel;
  @Inject UserProvider userProvider;
  final WorkmatesListAdapter workmatesListAdapter =
      new WorkmatesListAdapter(
          restaurantId -> RestaurantDetailsActivity.navigate(restaurantId, getActivity()));

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final FragmentWorkmatesBinding binding =
        FragmentWorkmatesBinding.inflate(
            LayoutInflater.from(container.getContext()), container, false);
    binding.workmatesList.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    binding.workmatesList.setAdapter(workmatesListAdapter);
    handleWorkplaceRequiredMessageVisibility(binding);
    intiViewMode(container);
    viewModel.getCurrentUserWorkmates(getWorkmatesRequestCallback());
    return binding.getRoot();
  }

  private void handleWorkplaceRequiredMessageVisibility(FragmentWorkmatesBinding binding) {
    final User currentUser = userProvider.getCurrentUser();
    if (currentUser.getWorkplaceId() == null || currentUser.getWorkplaceId().isEmpty()) {
      binding.workplaceRequiredMessage.setVisibility(View.VISIBLE);
      binding.selectMyWorkplaceAction.setOnClickListener(
          v -> {
            ((MainActivity) getActivity())
                .showWorkplacePiker(
                    () -> binding.workplaceRequiredMessage.setVisibility(View.GONE));
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
