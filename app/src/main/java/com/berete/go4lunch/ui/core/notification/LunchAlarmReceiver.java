package com.berete.go4lunch.ui.core.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LunchAlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("LunchAlarmReceiver", "onReceive");
    LunchTimeNotificationService.enqueue(context, intent);
  }
}
