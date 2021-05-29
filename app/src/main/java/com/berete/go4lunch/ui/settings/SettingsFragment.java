package com.berete.go4lunch.ui.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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
    new DeleteConfirmationDialog(this::deleteUserAccount)
        .show(getParentFragmentManager(), getString(R.string.delete_confirmation_dialog_TAG));
    return true;
  }

  private void deleteUserAccount() {
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
  }

  @Override
  public void onDisplayPreferenceDialog(Preference preference) {
    if (preference instanceof TimePreference) {
      final TimePreferencePickerDialog dialog =
          TimePreferencePickerDialog.newInstance(preference.getKey());
      // The Fragment.setTargetFragment() method is deprecated but for now, I have to use it
      // because the latest version of androidx.preference (1.1.1 at this time) uses the
      // Fragment.getTargetFragment() in the onCreate method of PreferenceDialogFragmentCompat which
      // require the call of the methode below.
      dialog.setTargetFragment(this, 0);
      dialog.show(getParentFragmentManager(), getString(R.string.time_preference_dialog_TAG));
    } else {
      super.onDisplayPreferenceDialog(preference);
    }
  }

  public static class DeleteConfirmationDialog extends DialogFragment {

    private final Runnable onPositiveButtonClicked;

    public DeleteConfirmationDialog(Runnable onPositiveButtonClicked) {
      this.onPositiveButtonClicked = onPositiveButtonClicked;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
      return new AlertDialog.Builder(requireContext())
          .setTitle(R.string.delete_confirmation_dialogue_title)
          .setMessage(R.string.ask_account_deletion_confirmation)
          .setNegativeButton(R.string.cancel_txt, (dialog, which) -> dismiss())
          .setPositiveButton(R.string.delete_txt, (dialog, which) -> onPositiveButtonClicked.run())
          .create();
    }
  }
}
