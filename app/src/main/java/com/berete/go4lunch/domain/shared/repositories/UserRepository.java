package com.berete.go4lunch.domain.shared.repositories;

import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.inject.Inject;

public class UserRepository {
  private final UserProvider userProvider;

  @Inject
  public UserRepository(UserProvider userProvider) {
    this.userProvider = userProvider;
  }

  public void getUserById(String userId, Callback<User> callback) {
    userProvider.getUserById(userId, callback);
  }

  public void getCurrentUserWorkmates(Callback<User[]> callback) {
    userProvider.getUsersByWorkplace(
        userProvider.getCurrentUser().getWorkplaceId(),
        new Callback<User[]>() {
          @Override
          public void onSuccess(User[] users) {
            // The method should provide only the current user workmates, so the current user must
            // be removed.
            final Stream<User> filteredUsers =
                Arrays.stream(users)
                    .filter(user -> !user.getId().equals(userProvider.getCurrentUser().getId()));
            callback.onSuccess(filteredUsers.toArray(User[]::new));
          }

          @Override
          public void onFailure() {
            callback.onFailure();
          }
        });
  }

  public void getUsersByChosenRestaurant(
      String restaurantId, String usersWorkplaceId, Callback<User[]> callback) {
    userProvider.getUsersByChosenRestaurant(restaurantId, usersWorkplaceId, callback);
  }

  public User getCurrentUser() {
    return userProvider.getCurrentUser();
  }
}
