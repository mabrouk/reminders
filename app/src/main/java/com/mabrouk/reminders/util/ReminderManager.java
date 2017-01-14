package com.mabrouk.reminders.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.mabrouk.reminders.services.LocationReminderService;
import com.mabrouk.reminders.model.Reminder;
import com.mabrouk.reminders.receivers.TimeReminderReceiver;

import java.util.Calendar;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

/**
 * Created by ahmad on 1/13/17.
 */

public class ReminderManager {
    public static void createGeofenceForReminder(ReactiveLocationProvider locationProvider, Reminder reminder,
                                                                 Context context) {
        Geofence geofence = new Geofence.Builder().setRequestId(String.valueOf(reminder.getId()))
                .setCircularRegion(reminder.getLatitude(), reminder.getLongitude(), Constants.GEOFENCE_RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder().addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        Intent intent = new Intent(context, LocationReminderService.class);
        intent.putExtra(LocationReminderService.EXTRA_REMINDER, reminder);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        locationProvider.addGeofences(pendingIntent, request)
        .subscribe(status -> Log.d("ZZZ", "geo succ " + status), throwable -> throwable.printStackTrace());
    }

    public static void createAlarmForReminder(Reminder reminder, Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, TimeReminderReceiver.class);
        alarmIntent.putExtra(TimeReminderReceiver.EXTRA_REMINDER, reminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTimestamp(), pendingIntent);
        }else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, reminder.getTimestamp(), pendingIntent);
        }
    }

    private ReminderManager() {
        throw new IllegalStateException(getClass().getSimpleName() + " is a util class and shouldn't be instantiated");
    }
}
