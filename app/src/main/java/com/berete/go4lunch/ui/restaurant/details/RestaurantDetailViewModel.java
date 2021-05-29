package com.berete.go4lunch.ui.restaurant.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.repositories.RestaurantDetailsRepository;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.shared.repositories.UserRepository;
import com.berete.go4lunch.domain.utils.Callback;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantDetailViewModel extends ViewModel {

  private static final Place.Field[] DEFAULT_FIELDS = {
    Place.Field.ID,
    Place.Field.NAME,
    Place.Field.ADDRESS,
    Place.Field.PHONE_NUMBER,
    Place.Field.WEBSITE_URL,
    Place.Field.RATE,
    Place.Field.PHOTO_URL
  };

  private final RestaurantDetailsRepository restaurantDetailsRepository;
  private final UserRepository userRepository;
  private final MutableLiveData<User> currentUser = new MutableLiveData<>();

  @Inject
  public RestaurantDetailViewModel(
      RestaurantDetailsRepository restaurantDetailsRepository, UserRepository userRepository) {
    this.restaurantDetailsRepository = restaurantDetailsRepository;
    this.userRepository = userRepository;
    userRepository.addUserLoginCompleteListener(currentUser::setValue);
    currentUser.setValue(userRepository.getCurrentUser());
  }

  public void getRestaurantDetails(String restaurantId, Callback<Place> listener) {
    restaurantDetailsRepository.getRestaurantDetails(
        restaurantId, DEFAULT_FIELDS, Place.LangCode.getSystemLanguage(), listener);
  }

  public void getWorkmatesByChosenRestaurant(String restaurantId, Callback<User[]> callback) {
    final String currentUserWorkplaceId = userRepository.getCurrentUser().getWorkplaceId();
    if (currentUserWorkplaceId == null) {
      callback.onSuccess(new User[0]);
    }
    userRepository.getUsersByChosenRestaurant(
        restaurantId,
        currentUserWorkplaceId,
        new Callback<User[]>() {
          @Override
          public void onSuccess(User[] users) {
            // The method should provide only the current user workmates, so the current user must
            // be removed.
            final Stream<User> filteredUsers =
                Arrays.stream(users)
                    .filter(user -> !user.getId().equals(userRepository.getCurrentUser().getId()));
            callback.onSuccess(filteredUsers.toArray(User[]::new));
          }

          @Override
          public void onFailure() {}
        });
  }

  public LiveData<User> getCurrentUser() {
    return currentUser;
  }

  public void resetCurrentUserChosenRestaurant() {
    userRepository.resetCurrentUserChosenRestaurant();
  }

  public void updateUserData(String dataType, Object data) {
    userRepository.updateUserData(dataType, data);
  }
}
