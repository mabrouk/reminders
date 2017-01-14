package com.mabrouk.reminders.util;

import android.app.Application;

import com.mabrouk.reminders.db.DBAccessor;

/**
 * Created by ahmad on 1/13/17.
 */

public class ReminderApplication extends Application {
    private static ReminderApplication instance = null;
    public static ReminderApplication getInstance() {
        return instance;
    }
    private static RxBus rxBusInstance;
    public static RxBus getRxBusInstance() {
        return rxBusInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DBAccessor.init(this);
        rxBusInstance = new RxBus();
    }
}
