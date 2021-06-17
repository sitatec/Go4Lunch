package com.berete.go4lunch;

import android.content.Context;

import androidx.annotation.CallSuper;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.berete.go4lunch.domain.shared.repositories.UserRepository;
import com.berete.go4lunch.ui.core.activities.EntryPointActivity;

import org.junit.Before;
import org.junit.Rule;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@HiltAndroidTest
public abstract class BaseTest {

  @Rule(order = 0)
  public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

  @Rule(order = 1)
  public ActivityScenarioRule<EntryPointActivity> activityScenarioRule =
      new ActivityScenarioRule<>(EntryPointActivity.class);

  @Rule
  public GrantPermissionRule runtimePermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @Inject public UserRepository userRepository;

  protected final Context context = ApplicationProvider.getApplicationContext();

  UiDevice uiDevice;

  @Before
  public void init() {
    if (userRepository == null) {
      hiltRule.inject();
    }
    if (uiDevice == null) {
      uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }
  }

  // ------------------------- UTILS ------------------------ //

  public void waitForWindowUpdate(){
    uiDevice.waitForWindowUpdate(BuildConfig.APPLICATION_ID, 5000);
  }

  public void loginUser() throws UiObjectNotFoundException {
    uiDevice.waitForWindowUpdate(BuildConfig.APPLICATION_ID, 1000);
    onView(withId(R.id.google_login_btn)).perform(click());

    uiDevice.waitForWindowUpdate(BuildConfig.APPLICATION_ID, 1000);
    UiObject emailText = uiDevice.findObject(new UiSelector().text("sita.berete.3@gmail.com"));
    emailText.click();
    uiDevice.waitForWindowUpdate(BuildConfig.APPLICATION_ID, 1000);
  }
}
