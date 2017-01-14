package com.mabrouk.reminders.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mabrouk.reminders.R;
import com.mabrouk.reminders.ReminderApplication;
import com.mabrouk.reminders.db.DBAccessor;
import com.mabrouk.reminders.model.Reminder;
import com.mabrouk.reminders.util.NotificationUtil;
import com.mabrouk.reminders.util.RxBus;

import java.util.Arrays;
import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

/**
 * Created by ahmad on 1/13/17.
 */

public class LocationReminderService extends IntentService {
    public static final String EXTRA_REMINDER = "reminder";
    public LocationReminderService() {
        super("LocationReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        Reminder reminder = intent.getParcelableExtra(EXTRA_REMINDER);
        if(DBAccessor.getInstance() == null) {
            Log.d("ZZZ", "DBAcessor = null " + LocationReminderService.class.getName());
            DBAccessor.init(this);
        }
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
        DBAccessor.getInstance().updateCompleted(reminder.getId());

        List<Geofence> geofences = event.getTriggeringGeofences();
        if(geofences != null) {
            Observable.from(geofences)
                    .map(geofence -> geofence.getRequestId())
                    .toList().subscribe(requestIds -> locationProvider.removeGeofences(requestIds));
        }else{
            //since the list returned from the event is null, and the reminder id was used as request id
            //will use it to remove the geofence request
            locationProvider.removeGeofences(Arrays.asList(String.valueOf(reminder.getId())))
            .subscribe(status -> Log.d("ZZZ", "remove geofence " + status), throwable -> throwable.printStackTrace());
        }

        NotificationUtil.createNotificationForReminder(reminder, this);
        RxBus bus = ReminderApplication.getRxBusInstance();
        if(bus.hasObservers()) {
            bus.send(reminder);
        }
    }
}
