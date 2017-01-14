package com.mabrouk.reminders.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ahmad on 1/13/17.
 */

class ReminderTable {
    static final String TABLE_NAME = "Reminders";
    static final String COLUMN_ID = "id";
    static final String COLUMN_TYPE = "type";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_TIMESTAMP = "timestamp";
    static final String COLUMN_LATITUDE = "latitude";
    static final String COLUMN_LONGITUDE = "longitude";
    static final String COLUMN_COMPLETED = "completed";

    static final String CREATION_STATEMENT = "CREATE TABLE " + TABLE_NAME + "( " + COLUMN_ID + " INTEGER "
            + "PRIMARY KEY AUTOINCREMENT, " + COLUMN_TYPE + " INTEGER, " + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_TIMESTAMP + " INTEGER, " + COLUMN_LATITUDE + " REAL, " + COLUMN_LONGITUDE + " REAL, "
            + COLUMN_COMPLETED + " INTEGER" + ");";
}
