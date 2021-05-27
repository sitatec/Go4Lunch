package com.berete.go4lunch.ui.core.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class LunchAlarmManager {

  public static final int PENDING_INTENT_REQUEST_CODE = 11;
  private final Context context;
  private final AlarmManager alarmManager;

  public LunchAlarmManager(Context context) {
    this.context = context;
    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
  }

  public void scheduleAlarm(Calendar calendar, Class<?> alarmReceiverClass) {
    final PendingIntent pendingIntent =
        getPendingIntent(alarmReceiverClass, PendingIntent.FLAG_UPDATE_CURRENT);
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.getTimeInMillis(),
        AlarmManager.INTERVAL_DAY,
        pendingIntent);
  }

  public void cancelAlarm(Class<?> alarmReceiverClass) {
    final PendingIntent pendingIntent =
        getPendingIntent(alarmReceiverClass, PendingIntent.FLAG_NO_CREATE);
    alarmManager.cancel(pendingIntent);
  }

  private PendingIntent getPendingIntent(Class<?> alarmReceiverClass, int flag) {
    final Intent notificationPendingIntent = new Intent(context, alarmReceiverClass);
    return PendingIntent.getBroadcast(
        context, PENDING_INTENT_REQUEST_CODE, notificationPendingIntent, flag);
  }
}
