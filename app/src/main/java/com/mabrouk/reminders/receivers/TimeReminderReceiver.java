package com.mabrouk.reminders.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mabrouk.reminders.util.ReminderApplication;
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
