package com.mabrouk.reminders.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mabrouk.reminders.model.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmad on 1/13/17.
 */

public class DBAccessor {
    static final String DB_NAME = "reminders.db";
    static DBAccessor instance;

    public static DBAccessor getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new DBAccessor(context, DB_NAME);
    }

    DBHelper dbHelper;
    private DBAccessor(Context context, String name){
        dbHelper = new DBHelper(context, name);
    }

    public List<Reminder> getActiveReminders() {
        return getReminders(0);
    }

    public List<Reminder> getCompletedReminders() {
        return getReminders(1);
    }

    List<Reminder> getReminders(int completed) {
        String query = "SELECT * FROM " + ReminderTable.TABLE_NAME + " WHERE " + ReminderTable.COLUMN_COMPLETED +
                " = ? ORDER BY " + ReminderTable.COLUMN_ID;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{String.valueOf(completed)});
        List<Reminder> reminders = new ArrayList<>(cursor.getCount());
        while(cursor.moveToNext()) {
            reminders.add(ReminderMapper.reminderFromCursor(cursor));
        }
        return reminders;
    }

    public Reminder createReminder(Reminder source) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(ReminderTable.TABLE_NAME, null, ReminderMapper.valuesOfReminder(source));
        if(id != -1) {
            return ReminderMapper.addIdToReminder(id, source);
        }else{
            return null;
        }
    }

    public boolean updateCompleted(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ReminderTable.COLUMN_COMPLETED, 1);
        return db.update(ReminderTable.TABLE_NAME, values, ReminderTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}) == 1;
    }
}
