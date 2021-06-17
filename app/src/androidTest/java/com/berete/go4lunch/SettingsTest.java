package com.berete.go4lunch;

import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.ui.settings.TimePreference;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@HiltAndroidTest
public class SettingsTest extends BaseTest {

  @Before
  public void setUp() {

    onView(withId(R.id.main_activity_root)).perform(DrawerActions.open());
    onView(withId(R.id.navigation_view))
        .perform(NavigationViewActions.navigateTo(R.id.settingsFragment));
  }

  @Test
  public void should_pick_a_time_for_the_lunch_time_notification()
      throws UiObjectNotFoundException {
    // TODO refactoring
    //

    onView(withText(R.string.lunch_reminder_time)).perform(click());
    onView(isAssignableFrom(TimePicker.class))
        .inRoot(RootMatchers.isDialog())
        .perform(setTimePickerValues(7, 30));
    onView(withText("Set")).inRoot(RootMatchers.isDialog()).perform(click());
    //    uiDevice.findObject(new UiSelector().text("Set")).click();

    waitForWindowUpdate();
    // The time should be "07:30 AM" or "07:30" depending on the device language
    onView(withText(startsWith("07:30"))).check(matches(isDisplayed()));
  }

  @Test
  public void the_lunch_time_picker_should_not_be_disabled() {
    onView(withText(R.string.toggle_notification)).perform(click()); // disable notification

    onView(withText(R.string.lunch_reminder_time)).perform(click());
    assertFalse(
        uiDevice
            .findObject(new UiSelector().text("Set"))
            .exists()); // the positive button of the time
    // picker should not be displayed - TODO: find a better way to test this case

    onView(withText(R.string.toggle_notification)).perform(click()); // enable notification
  }

  @Test
  public void should_show_confirmation_dialog_when_trying_to_delete_the_user_account() {
    onView(withText(R.string.delete_account_btn_txt)).perform(click());
    onView(withText(R.string.ask_account_deletion_confirmation)).check(matches(isDisplayed()));
  }

  // -------------------- UTILS ---------------------- //

  public ViewAction setTimePickerValues(int hours, int minutes) {
    return new ViewAction() {
      @Override
      public Matcher<View> getConstraints() {
        return ViewMatchers.isAssignableFrom(TimePicker.class);
      }

      @Override
      public String getDescription() {
        return "setTimePickerValues";
      }

      @Override
      public void perform(UiController uiController, View view) {
        final TimePicker timePicker = (TimePicker) view;
        timePicker.setHour(hours);
        timePicker.setMinute(minutes);
      }
    };
  }
}
