package com.berete.go4lunch;

import android.util.Log;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;

import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.ui.core.view_models.shared.RestaurantRelatedViewModel;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@HiltAndroidTest
public class NavigationDrawerTest extends BaseTest {

  @Before
  public void setUp() throws Exception {
    if(userRepository.getCurrentUser() == null){
      loginUser();
    }
    onView(withId(R.id.main_activity_root)).perform(DrawerActions.open());
    final User currentUser = userRepository.getCurrentUser();
    if (currentUser.getChosenRestaurantId().isEmpty()) {
      Thread.sleep(1000); // Wait to be sure that the restaurants have been fetched.
      final Restaurant randomRestaurant = RestaurantRelatedViewModel.getLastRequestResult()[0];
      currentUser.setChosenRestaurantId(randomRestaurant.getId());
      currentUser.setChosenRestaurantName(randomRestaurant.getName());
    }
  }

  @Test
  public void should_display_the_restaurant_chosen_by_the_current_user()
      throws InterruptedException {
    final String currentUserChosenRestaurantName =
        userRepository.getCurrentUser().getChosenRestaurantName();

    onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.user_lunch));
    Thread.sleep(500);
    onView(withId(R.id.restaurantDetailsRootLayout)).check(matches(isDisplayed()));
    onView(withId(R.id.collapsingToolbarLayout))
        .check(matches(withContentDescription(currentUserChosenRestaurantName)));
    Log.d("NAV_DRAWER_TEST", "END");
  }

  @Test
  public void should_logout_the_current_user() throws InterruptedException {
    onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.logout));
    Thread.sleep(500);
    Assert.assertNull(FirebaseAuth.getInstance().getCurrentUser());
    onView(withId(R.id.login_view_root)).check(matches(isDisplayed()));
  }

}
