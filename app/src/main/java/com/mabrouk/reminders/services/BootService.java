package com.mabrouk.reminders.services;

import android.app.IntentService;
import android.content.Intent;

import com.mabrouk.reminders.db.DBAccessor;
import com.mabrouk.reminders.model.Reminder;
import com.mabrouk.reminders.receivers.BootReceiver;
import com.mabrouk.reminders.util.ReminderManager;

import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

/**
 * Created by ahmad on 1/14/17.
 */

public class BootService extends IntentService {
    public BootService() {
        super("Boot service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(DBAccessor.getInstance() == null)
            DBAccessor.init(this);
        List<Reminder> active = DBAccessor.getInstance().getActiveReminders();
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
        for(Reminder reminder : active) {
            if(reminder.getType() == Reminder.TYPE_LOCATION_REMINDER) {
                ReminderManager.createGeofenceForReminder(locationProvider, reminder, this);
            }else{
                if(reminder.getTimestamp() < System.currentTimeMillis()) {
                    DBAccessor.getInstance().updateCompleted(reminder.getId());
                    continue;
                }

                ReminderManager.createAlarmForReminder(reminder, this);
            }
        }
        BootReceiver.completeWakefulIntent(intent);
    }
}
