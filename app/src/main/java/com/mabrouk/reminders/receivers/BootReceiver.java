package com.mabrouk.reminders.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.mabrouk.reminders.services.BootService;

/**
 * Created by ahmad on 1/14/17.
 */

public class BootReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BootService.class);
        startWakefulService(context, serviceIntent);
    }
}
