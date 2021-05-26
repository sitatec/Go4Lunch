package com.berete.go4lunch.ui.settings;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.PreferenceDialogFragmentCompat;

import java.util.Locale;

public class TimePreferencePickerDialog extends PreferenceDialogFragmentCompat {

  public static TimePreferencePickerDialog newInstance(String preferenceKey) {
    final Bundle bundle = new Bundle();
    bundle.putString(ARG_KEY, preferenceKey);
    final TimePreferencePickerDialog instance = new TimePreferencePickerDialog();
    instance.setArguments(bundle);
    return instance;
  }

  private TimePicker timePicker;

  @Override
  protected View onCreateDialogView(Context context) {
    super.onCreateDialogView(context);
    return timePicker = new TimePicker(context);
  }

  @Override
  public void onDialogClosed(boolean positiveResult) {
    if (positiveResult) {
      int minutesFromMidnight;
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        minutesFromMidnight = (timePicker.getCurrentHour() * 60) + timePicker.getCurrentMinute();
      } else {
        minutesFromMidnight = (timePicker.getHour() * 60) + timePicker.getMinute();
      }
      ((TimePreference) getPreference()).persistMinutesFromMidnight(minutesFromMidnight);
    }
  }

  @Override
  protected void onBindDialogView(View view) {
    super.onBindDialogView(view);
    final TimePreference timePreference = (TimePreference) getPreference();
    final int persistedMinutesFromMidnight = timePreference.getPersistedMinutesFromMidnight();
    timePicker.setIs24HourView(Locale.getDefault().getLanguage().equals("fr"));
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      timePicker.setCurrentMinute(persistedMinutesFromMidnight % 60);
      timePicker.setCurrentHour(persistedMinutesFromMidnight / 60);
    } else {
      timePicker.setMinute(persistedMinutesFromMidnight % 60);
      timePicker.setHour(persistedMinutesFromMidnight / 60);
    }
  }
}
