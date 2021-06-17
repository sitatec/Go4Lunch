package com.berete.go4lunch;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@HiltAndroidTest
public class WorkmatesFragmentTest extends BaseTest {


  @Before
  public void setUp() {
    onView(withId(R.id.workmatesListFragment)).perform(click());
  }

  @Test
  public void should_contains_the_current_users_workmates() throws InterruptedException {
    final List<User> currentUserWorkmates = getCurrentUserWorkmates();
    onView(withId(R.id.workmates_list))
        .check(recyclerViewWithItemCount(currentUserWorkmates.size()));
  }

  @Test
  public void
      clicking_on_a_workmate_item_should_display_him_chosen_restaurant_if_he_has_chosen_one()
          throws InterruptedException {

    final List<User> currentUserWorkmates = getCurrentUserWorkmates();
    final int randomWorkmateIndex = new Random().nextInt(currentUserWorkmates.size());

    onView(withId(R.id.workmates_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(randomWorkmateIndex, click()));
    onView(withId(R.id.restaurantDetailsRootLayout)).check(matches(isDisplayed()));

    final User randomWorkmate = currentUserWorkmates.get(randomWorkmateIndex);
    onView(withId(R.id.collapsingToolbarLayout))
        .check(matches((withContentDescription(randomWorkmate.getChosenRestaurantName()))));
  }

  // ------------------- UTILS ------------------ //

  public List<User> getCurrentUserWorkmates() throws InterruptedException {
    final Object threadsLocks = new Object();
    final List<User> currentUserWorkmates = new ArrayList<>();

    userRepository.getCurrentUserWorkmates(
        new Callback<User[]>() {
          @Override
          public void onSuccess(User[] users) {
            Collections.addAll(currentUserWorkmates, users);
            synchronized (threadsLocks) {
              threadsLocks.notify(); // unblock the thread
            }
          }

          @Override
          public void onFailure() {}
        });

    synchronized (threadsLocks) {
      threadsLocks.wait(5000); // block the tread until the workmates data have been fetched
    }
    return currentUserWorkmates;
  }

  public ViewAssertion recyclerViewWithItemCount(int itemCount) {
    return new ViewAssertion() {
      @Override
      public void check(View view, NoMatchingViewException noViewFoundException) {
        Log.d("Workmates_tests", "recyclerViewWithItemCount");
        if (noViewFoundException != null) {
          throw noViewFoundException;
        }

        final RecyclerView.Adapter adapter = ((RecyclerView) view).getAdapter();

        Log.d(
            "Workmates_tests",
            "expected_count : " + itemCount + "\n actual_count : " + adapter.getItemCount());
        Assert.assertEquals(adapter.getItemCount(), itemCount);
      }
    };
  }
}
