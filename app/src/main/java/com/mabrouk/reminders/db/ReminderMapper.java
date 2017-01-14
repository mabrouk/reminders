package com.mabrouk.reminders.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.mabrouk.reminders.model.Reminder;

/**
 * Created by ahmad on 1/13/17.
 */

class ReminderMapper {
    static Reminder reminderFromCursor(Cursor cursor) {
        int type = cursor.getInt(cursor.getColumnIndex(ReminderTable.COLUMN_TYPE));
        long id = cursor.getLong(cursor.getColumnIndex(ReminderTable.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(ReminderTable.COLUMN_NAME));
        boolean completed = cursor.getInt(cursor.getColumnIndex(ReminderTable.COLUMN_COMPLETED)) == 1;

        if(type == Reminder.TYPE_TIME_REMINDER) {
            long timestamp = cursor.getLong(cursor.getColumnIndex(ReminderTable.COLUMN_TIMESTAMP));
            return Reminder.createTimeReminder(id, name, timestamp, completed);
        } else if(type == Reminder.TYPE_LOCATION_REMINDER){
            double latitude = cursor.getDouble(cursor.getColumnIndex(ReminderTable.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(ReminderTable.COLUMN_LONGITUDE));
            return Reminder.createLocationReminder(id, name, latitude, longitude, completed);
        } else {
            throw new IllegalStateException("Cannot create reminder with type " + type + "; source database");
        }
    }

    static ContentValues valuesOfReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(ReminderTable.COLUMN_TYPE, reminder.getType());
        values.put(ReminderTable.COLUMN_NAME, reminder.getName());
        values.put(ReminderTable.COLUMN_COMPLETED, reminder.isCompleted() ? 1 : 0);
        values.put(ReminderTable.COLUMN_TIMESTAMP, reminder.getTimestamp());
        values.put(ReminderTable.COLUMN_LATITUDE, reminder.getLatitude());
        values.put(ReminderTable.COLUMN_LONGITUDE, reminder.getLongitude());
        return values;
    }
}
