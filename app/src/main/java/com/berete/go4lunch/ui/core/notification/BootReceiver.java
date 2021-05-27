package com.berete.go4lunch.ui.core.notification;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.berete.go4lunch.ui.settings.TimePreference;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    final Calendar calendar = new TimePreference(context).getPersistedTimeAsCalendar();
    new LunchAlarmManager(context).scheduleAlarm(calendar, LunchAlarmReceiver.class);
  }

  public static void enable(Context context) {
    setState(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
  }

  public static void disable(Context context) {
    setState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
  }

  private static void setState(Context context, int state) {
    final ComponentName receiver = new ComponentName(context, BootReceiver.class);
    context
        .getPackageManager()
        .setComponentEnabledSetting(receiver, state, PackageManager.DONT_KILL_APP);
  }
}
