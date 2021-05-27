package com.berete.go4lunch.ui.settings;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.berete.go4lunch.R;
import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.utils.Callback;
import com.berete.go4lunch.ui.core.activities.MainActivity;
import com.berete.go4lunch.ui.core.notification.LunchTimeNotification;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends PreferenceFragmentCompat {

  @Inject public UserProvider userProvider;
  CheckBoxPreference notificationPreference;
  TimePreference timePreference;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.main_preferences, rootKey);
    setHasOptionsMenu(true);
    notificationPreference = findPreference("notification_enabled");
    timePreference = findPreference("reminder_time");
    if (notificationPreference != null && timePreference != null) {
      timePreference.setEnabled(notificationPreference.isChecked());
      notificationPreference.setOnPreferenceChangeListener(this::onNotificationPreferencesChange);
    }
    final Preference workplacePreference = findPreference("workplace");
    workplacePreference.setSummary(
        workplacePreference.getSharedPreferences().getString("workplace", "Not set"));
    workplacePreference.setOnPreferenceClickListener(this::onWorkplacePreferencesClick);
    findPreference("delete_account").setOnPreferenceClickListener(this::onDeleteAccountClick);
  }

  @Override
  public void onPrepareOptionsMenu(@NonNull Menu menu) {
    menu.findItem(R.id.searchAction).setVisible(false);
    super.onPrepareOptionsMenu(menu);
  }

  private boolean onNotificationPreferencesChange(Preference preference, Object newValue) {
    final boolean enabled = (boolean) newValue;
    timePreference.setEnabled(enabled);
    LunchTimeNotification.setEnabled(
        getContext(), enabled, timePreference.getPersistedTimeAsCalendar());
    return true;
  }

  private boolean onWorkplacePreferencesClick(Preference preference) {
    final MainActivity mainActivity = ((MainActivity) getActivity());
    mainActivity.showWorkplacePiker(
        workplacePrediction ->
            preference.setSummary(SettingsUtils.toFormattedWorkplaceSummary(workplacePrediction)));
    return true;
  }

  private boolean onDeleteAccountClick(Preference __) {
    userProvider.deleteCurrentUserAccount(
        new Callback<Boolean>() {
          @Override
          public void onSuccess(Boolean aBoolean) {
            Snackbar.make(getView(), R.string.account_deletion_confirmation, Snackbar.LENGTH_SHORT)
                .addCallback(
                    new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                      @Override
                      public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        getActivity().finish();
                      }
                    })
                .show();
          }

          @Override
          public void onFailure() {
            Snackbar.make(getView(), R.string.account_deletion_failed_msg, Snackbar.LENGTH_SHORT)
                .show();
          }
        });
    return true;
  }

  @Override
  public void onDisplayPreferenceDialog(Preference preference) {
    if (preference instanceof TimePreference) {
      final TimePreferencePickerDialog dialog =
          TimePreferencePickerDialog.newInstance(preference.getKey());
      dialog.setTargetFragment(this, 0);
      //      dialog.getParentFragmentManager().setFragmentResultListener(dialo);
      dialog.show(getParentFragmentManager(), "The time preference picker dialog");
    } else {
      super.onDisplayPreferenceDialog(preference);
    }
  }
}
