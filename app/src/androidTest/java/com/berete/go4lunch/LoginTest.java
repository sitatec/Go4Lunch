package com.berete.go4lunch;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.berete.go4lunch.ui.core.activities.EntryPointActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest extends BaseTest {

  // TODO use firebase emulators.

  @Test
  public void a(){}

  @Test
  public void should_login_with_google_sign_in() throws UiObjectNotFoundException {
    if(FirebaseAuth.getInstance().getCurrentUser() != null){
      AuthUI.getInstance().signOut(ApplicationProvider.getApplicationContext());
    }
    waitForWindowUpdate();

    loginUser();

    onView(withId(R.id.main_activity_root)).check(matches(isDisplayed()));
    AuthUI.getInstance().signOut(ApplicationProvider.getApplicationContext());
  }

  @Test
  public void should_show_login_canceled_error_message() {
    waitForWindowUpdate();
    pressBack(); // cancel the login process
    onView(
            withText(
                ApplicationProvider.getApplicationContext()
                    .getResources()
                    .getString(R.string.login_canceled)))
        .check(matches(isDisplayed()));
    Log.d("LOGIN_TEST", "here");
  }

  @Test
  public void should_retry_login() {
    waitForWindowUpdate();
    pressBack(); // cancel the login process
    onView(withId(R.id.entry_point_root)).check(matches(isDisplayed()));

    onView(withId(R.id.retry_login)).perform(click());
    onView(withId(R.id.login_view_root)).check(matches(isDisplayed()));
    Log.d("LOGIN_TEST", "here");
  }
}
