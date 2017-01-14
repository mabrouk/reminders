package com.mabrouk.reminders.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mabrouk.reminders.MainActivity;
import com.mabrouk.reminders.R;
import com.mabrouk.reminders.ReminderDetailsActivity;
import com.mabrouk.reminders.model.Reminder;

/**
 * Created by ahmad on 1/13/17.
 */

public class NotificationUtil {

    public static void createNotificationForReminder(Reminder reminder, Context context) {
        Intent intent = ReminderDetailsActivity.createIntent(context, reminder);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(reminder.getName())
                .setContentText("A reminder you specified has fired")
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) reminder.getId(), notification);
    }

    private NotificationUtil() {
        throw new IllegalStateException(getClass().getSimpleName() + " is a util class and shouldn't be instantiated");
    }
}
