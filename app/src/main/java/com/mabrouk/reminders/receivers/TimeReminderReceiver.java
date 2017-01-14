package com.mabrouk.reminders.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mabrouk.reminders.R;
import com.mabrouk.reminders.ReminderApplication;
import com.mabrouk.reminders.db.DBAccessor;
import com.mabrouk.reminders.model.Reminder;
import com.mabrouk.reminders.util.NotificationUtil;
import com.mabrouk.reminders.util.RxBus;

/**
 * Created by ahmad on 1/13/17.
 */

public class TimeReminderReceiver extends BroadcastReceiver{
    public static final String EXTRA_REMINDER = "reminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(DBAccessor.getInstance() == null) {
            DBAccessor.init(context);
        }
        Reminder reminder = intent.getParcelableExtra(EXTRA_REMINDER);
        DBAccessor.getInstance().updateCompleted(reminder.getId());

        NotificationUtil.createNotificationForReminder(reminder, context);
        RxBus bus = ReminderApplication.getRxBusInstance();
        if(bus.hasObservers()) {
            bus.send(reminder);
        }
    }
}
