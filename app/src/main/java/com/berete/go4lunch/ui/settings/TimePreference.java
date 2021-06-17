package com.berete.go4lunch.ui.settings;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

import java.util.Calendar;
import java.util.Locale;

public class TimePreference extends DialogPreference {

  public static int DEFAULT_TIME_PREFERENCE_IN_MINUTES_FROM_MIDNIGHT = 12 * 60;

  public TimePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    initializes();
  }

  public TimePreference(Context context) {
    super(context);
    initializes();
  }

  private void setDialogButtonsText() {
    setPositiveButtonText("Set");
    setNegativeButtonText("Cancel");
  }

  private void initializes() {
    setDialogButtonsText();
    setSummary(getFormattedTime(Locale.getDefault()));
  }

  public int getPersistedMinutesFromMidnight() {
    return getPersistedInt(DEFAULT_TIME_PREFERENCE_IN_MINUTES_FROM_MIDNIGHT);
  }

  public Calendar getPersistedTimeAsCalendar() {
    final Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, getPersistedMinutesFromMidnight() / 60);
    calendar.set(Calendar.MINUTE, getPersistedMinutesFromMidnight() % 60);
    return calendar;
  }

  public void persistMinutesFromMidnight(int minutesFromMidnight) {
    persistInt(minutesFromMidnight);
    setSummary(getFormattedTime(Locale.getDefault()));
    notifyChanged();
  }

  private String getFormattedTime(Locale locale) {
    if (locale.getLanguage().equals("fr")) {
      return getPersistedMinutesAs24HourTimeFormat();
    }
    return getPersistedMinutesAs12HourTimeFormat();
  }

  private String getPersistedMinutesAs24HourTimeFormat() {
    final int minutesFromMidnight = getPersistedMinutesFromMidnight();
    return String.format(
        Locale.getDefault(), "%1$02d:%2$02d", minutesFromMidnight / 60, minutesFromMidnight % 60);
  }

  private String getPersistedMinutesAs12HourTimeFormat() {
    final int minutesFromMidnight = getPersistedMinutesFromMidnight();
    int hours = minutesFromMidnight / 60;
    if (hours > 12) {
      return String.format(
          Locale.getDefault(), "%1$02d:%2$02d PM", hours - 12, minutesFromMidnight % 60);
    }
    return String.format(Locale.getDefault(), "%1$02d:%2$02d AM", hours, minutesFromMidnight % 60);
  }

  @Override
  protected void onSetInitialValue(@Nullable Object defaultValue) {
    super.onSetInitialValue(defaultValue);
    setSummary(getFormattedTime(Locale.getDefault()));
  }
}
