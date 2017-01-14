package com.mabrouk.reminders.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mabrouk.reminders.model.Reminder;

/**
 * Created by ahmad on 1/13/17.
 */

class DBHelper extends SQLiteOpenHelper {
    static final int VERSION = 1;

    public DBHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ReminderTable.CREATION_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
