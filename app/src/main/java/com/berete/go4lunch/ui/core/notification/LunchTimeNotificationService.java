package com.berete.go4lunch.ui.core.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.repositories.RestaurantDetailsRepository;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.shared.repositories.UserRepository;
import com.berete.go4lunch.domain.utils.Callback;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LunchTimeNotificationService extends JobIntentService {

  private static final int NOTIFICATION_ID = 11;
  private static final int JOB_ID = 22;

  @Inject UserRepository userRepository;
  @Inject RestaurantDetailsRepository restaurantDetailsRepository;
  User currentUser;
  User[] workmatesWhoAreJoiningMyRestaurant;
  String restaurantAddress;
  private int dataFetchSuccessesCount = 0;
  // ^ must be 2 to display the notification (+1 for the restaurant address and +1 for the user
  // workmates)

  public static void enqueue(@NonNull Context context, @NonNull Intent intent){
    enqueueWork(context, LunchTimeNotificationService.class, JOB_ID, intent);
  }

  @Override
  protected void onHandleWork(@NonNull Intent intent) {
    currentUser = userRepository.getCurrentUser();
    if (!currentUser.getChosenRestaurantId().isEmpty()) {
      if (!currentUser.getWorkplaceId().isEmpty()) {
        userRepository.getUsersByChosenRestaurant(
            currentUser.getChosenRestaurantId(), currentUser.getWorkplaceId(), getUsersCallback());
      }
      restaurantDetailsRepository.getRestaurantDetails(
          currentUser.getChosenRestaurantId(),
          new Place.Field[] {Place.Field.ADDRESS},
          Place.LangCode.getSystemLanguage(),
          getRestaurantDetailsCallback());
    }
  }

  private void notifySuccessfulDataFetch() {
    if (++dataFetchSuccessesCount < 2) return;
    displayNotification();
  }

  private void displayNotification(){
    final NotificationManager notificationManager =
        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    final Notification notification = new LunchTimeNotification(getApplicationContext())
        .setUserToReminder(currentUser)
        .setLunchParticipants(workmatesWhoAreJoiningMyRestaurant)
        .setRestaurantAddress(restaurantAddress)
        .createNotificationChanelIfNeeded(notificationManager)
        .build();
    notificationManager.notify(NOTIFICATION_ID, notification);
  }

  private Callback<Place> getRestaurantDetailsCallback() {
    return new Callback<Place>() {
      @Override
      public void onSuccess(Place place) {
        LunchTimeNotificationService.this.restaurantAddress = place.getAddress();
        notifySuccessfulDataFetch();
      }

      @Override
      public void onFailure() {}
    };
  }

  private Callback<User[]> getUsersCallback() {
    return new Callback<User[]>() {
      @Override
      public void onSuccess(User[] users) {
        Log.d("LunchTimeNotifService", "Users fetched : " + Arrays.toString(users));
        workmatesWhoAreJoiningMyRestaurant = users;
        notifySuccessfulDataFetch();
      }

      @Override
      public void onFailure() {}
    };
  }
}
