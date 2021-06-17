package com.berete.go4lunch;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.UriMatchers;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.berete.go4lunch.domain.restaurants.models.Restaurant;
import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.ui.core.view_models.shared.RestaurantRelatedViewModel;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

@HiltAndroidTest
public class RestaurantRelatedTests extends BaseTest {


  @Before
  public void setUp() throws Exception {
    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
      loginUser();
    }
  }

  @Test
  public void should_select_a_restaurant_from_the_map_and_show_its_details()
      throws UiObjectNotFoundException {
    // TODO refactoring (improve readability)
    waitForWindowUpdate();
    final Restaurant fetchedRestaurant = RestaurantRelatedViewModel.getLastRequestResult()[0];
    Log.d("MAP_TEST", "Restaurant name : " + fetchedRestaurant.getName());
    // the marker title and contentDescription contain the restaurant name
    final UiObject restaurantMarker =
        uiDevice.findObject(new UiSelector().descriptionContains(fetchedRestaurant.getName()));

    // By default the map zoom level is not enough to make distance between the markers, so we need
    // to zoom in before selecting the right marker

    // Click on the marker or a marker around (depending on the markers density) to make sure that
    // the marker zone is in the map center before performing the zoom
    restaurantMarker.click();

    final UiObject mapViewContainer =
        uiDevice.findObject(
            new UiSelector().resourceId(BuildConfig.APPLICATION_ID + ":id/mapFragmentContainer"));
    // Zoom on the map view.
    mapViewContainer.pinchOut(50, 100);
    // Change the focus to make the info window disappear.
    mapViewContainer.click();

    // Click on the good Marker
    restaurantMarker.click();
    // Click on the info window
    uiDevice.click(uiDevice.getDisplayWidth() / 2, restaurantMarker.getBounds().top - 20);
    waitForWindowUpdate();

    onView(withId(R.id.restaurantDetailsRootLayout)).check(matches(isDisplayed()));

    onView(withId(R.id.collapsingToolbarLayout))
        .check(matches((withContentDescription(fetchedRestaurant.getName()))));

    final User currentUser = userRepository.getCurrentUser();
    // Choosing Restaurant
    userRepository.resetCurrentUserChosenRestaurant();
    // Choose
    assertTrue(currentUser.getChosenRestaurantId().isEmpty());
    onView(withId(R.id.actionChooseRestaurant)).perform(click());
    assertEquals(currentUser.getChosenRestaurantId(), fetchedRestaurant.getId());
    // Undo
    onView(withId(R.id.actionChooseRestaurant)).perform(click());
    //    uiDevice.waitForWindowUpdate(BuildConfig.APPLICATION_ID, 500);
    assertTrue(currentUser.getChosenRestaurantId().isEmpty());

    // Liking Restaurant
    currentUser.setLikedRestaurantsIds(new ArrayList<>());
    userRepository.updateUserData(UserProvider.LIKED_RESTAURANTS, new ArrayList<>());
    // Like
    onView(withId(R.id.likeButton)).perform(click());
    assertTrue(currentUser.getLikedRestaurantsIds().contains(fetchedRestaurant.getId()));
    // Undo
    onView(withId(R.id.likeButton)).perform(click());
    //    uiDevice.waitForWindowUpdate(BuildConfig.APPLICATION_ID, 500);
    assertTrue(currentUser.getConversationsIds().isEmpty());

    // Calling the restaurant
    Intents.init();
    intending(hasData(UriMatchers.hasScheme("tel")))
        .respondWith(
            new Instrumentation.ActivityResult(
                Activity.RESULT_OK, new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"))));
    onView(withId(R.id.callButton)).perform(click());
    intended(hasData(UriMatchers.hasScheme("tel")));

    // Launching the restaurant website
    intending(hasData(UriMatchers.hasScheme("https")))
        .respondWith(
            new Instrumentation.ActivityResult(
                Activity.RESULT_OK, new Intent(Intent.ACTION_VIEW, Uri.parse("http:"))));
    onView(withId(R.id.urlButton)).perform(click());
    intended(hasData(UriMatchers.hasScheme("https")));

    Intents.release();

    Log.d("MAP_TEST", "END");
  }

  @Test
  public void should_select_a_restaurant_from_the_restaurants_list() {
    onView(withId(R.id.restaurantListFragment)).perform(click());
    onView(withId(R.id.restaurants_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    onView(withId(R.id.restaurantDetailsRootLayout)).check(matches(isDisplayed()));
  }

  @Test
  public void should_search_a_restaurant() throws InterruptedException {
    onView(withId(R.id.searchAction)).perform(click());
    onView(isAssignableFrom(EditText.class)).perform(typeTextIntoFocusedView("ti"));
    Thread.sleep(500);
    onView(withId(R.id.predictionList))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    onView(withId(R.id.restaurantDetailsRootLayout)).check(matches(isDisplayed()));
    Log.d("SEARCH_TEST", "END");
  }

  @Test
  public void should_filter_restaurant_list_by_different_criteria() throws InterruptedException {
    Thread.sleep(500);
    onView(withId(R.id.restaurantListFragment)).perform(click());
    // By nearest
    onView(withId(R.id.orderRestaurants)).perform(click());
    onView(withText(R.string.order_by_nearest)).perform(click());
    onView(withId(R.id.restaurants_list)).check(itemsShouldBeSortedByNearest());
    // By stars (DESC)
    onView(withId(R.id.orderRestaurants)).perform(click());
    onView(withText(R.string.order_by_stars)).perform(click());
    onView(withId(R.id.restaurants_list)).check(itemsShouldBeSortedByStarsCount());
    // By the number of workmates who are chosen the restaurants
    onView(withId(R.id.orderRestaurants)).perform(click());
    onView(withText(R.string.order_by_workmates_count)).perform(click());
    onView(withId(R.id.restaurants_list)).check(itemsShouldBeSortedByWorkmatesCount());
  }

  // ----------------- UTILS --------------- //

  public ViewAssertion itemsShouldBeSortedByNearest() {
    return new ViewAssertion() {
      @Override
      public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
          throw noViewFoundException;
        }

        final RecyclerView recyclerView = (RecyclerView) view;
        int i = 0;
        View previousItemView = recyclerView.getChildAt(i);
        View currentItemView = recyclerView.getChildAt(++i);
        TextView previousItemText;
        TextView currentItemText;

        while (currentItemView != null) {
          previousItemText = previousItemView.findViewById(R.id.distanceFromCurrentLoc);
          currentItemText = currentItemView.findViewById(R.id.distanceFromCurrentLoc);
          // the distance is displayed like this: (distance m) e.g. 464m. So we need to extract the
          // number
          assertTrue(
              Double.parseDouble(previousItemText.getText().toString().replace("m", ""))
                  <= Double.parseDouble(currentItemText.getText().toString().replace("m", "")));

          previousItemView = currentItemView;
          currentItemView = recyclerView.getChildAt(++i);
        }
      }
    };
  }

  public ViewAssertion itemsShouldBeSortedByStarsCount() {
    return new ViewAssertion() {
      @Override
      public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
          throw noViewFoundException;
        }

        final RecyclerView recyclerView = (RecyclerView) view;
        int i = 0;
        View previousItemView = recyclerView.getChildAt(i);
        View currentItemView = recyclerView.getChildAt(++i);

        while (currentItemView != null) {
          assertTrue(
              ((Restaurant) currentItemView.getTag()).getStarsBoundedTo3()
                  <= ((Restaurant) previousItemView.getTag()).getStarsBoundedTo3());

          previousItemView = currentItemView;
          currentItemView = recyclerView.getChildAt(++i);
        }
      }
    };
  }

  public ViewAssertion itemsShouldBeSortedByWorkmatesCount() {
    return new ViewAssertion() {
      @Override
      public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
          throw noViewFoundException;
        }

        Log.d("MAP_TEST", "BEFORE");

        final RecyclerView recyclerView = (RecyclerView) view;
        int i = 0;
        View previousItemView = recyclerView.getChildAt(i);
        View currentItemView = recyclerView.getChildAt(++i);
        String previousItemText;
        String currentItemText;

        while (currentItemView != null) {
          previousItemText =
              ((TextView) previousItemView.findViewById(R.id.numberOfWorkmatesThere))
                  .getText()
                  .toString();
          currentItemText =
              ((TextView) currentItemView.findViewById(R.id.numberOfWorkmatesThere))
                  .getText()
                  .toString();

          if (currentItemText.isEmpty()) break;
          // the text is displayed like this: (workmates_count) e.g. (6). So we need to extract the
          // number
          assertTrue(
              Integer.parseInt(previousItemText.substring(1, previousItemText.length() - 1))
                  >= Integer.parseInt(currentItemText.substring(1, currentItemText.length() - 1)));

          previousItemView = currentItemView;
          currentItemView = recyclerView.getChildAt(++i);
        }

        Log.d("MAP_TEST", "AFTER");
      }
    };
  }
}
