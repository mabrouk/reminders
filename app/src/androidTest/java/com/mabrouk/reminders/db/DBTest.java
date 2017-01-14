package com.mabrouk.reminders.db;

/**
 * Created by ahmad on 1/14/17.
 */


import android.content.Context;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mabrouk.reminders.model.Reminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DBTest {
    Context appContext;

    @Before
    public void init() {
        appContext = InstrumentationRegistry.getTargetContext();
        DBAccessor.initForTesting(appContext);
        Reminder completedReminder = Reminder.createTimeReminder(0, "", System.currentTimeMillis(), true);
        Reminder active1 = Reminder.createLocationReminder(0, "", 32.0, 28.0, false);
        Reminder active2 = Reminder.createLocationReminder(0, "", -32.0, -28.0, false);
        DBAccessor.getInstance().createReminder(completedReminder);
        DBAccessor.getInstance().createReminder(active1);
        DBAccessor.getInstance().createReminder(active2);
    }

    @Test
    public void testQueryCompleted() {
        List<Reminder> completedReminders = DBAccessor.getInstance().getCompletedReminders();
        assertEquals(completedReminders.size(), 1);
    }

    @Test
    public void testQueryActive() {
        List<Reminder> activeReminders = DBAccessor.getInstance().getActiveReminders();
        assertEquals(activeReminders.size(), 2);
    }

    @Test
    public void testCompleteReminder() {
        List<Reminder> activeReminders = DBAccessor.getInstance().getActiveReminders();
        boolean result = DBAccessor.getInstance().updateCompleted(activeReminders.get(0).getId());
        activeReminders = DBAccessor.getInstance().getActiveReminders();
        List<Reminder> completedReminders = DBAccessor.getInstance().getCompletedReminders();
        assertTrue(result);
        assertEquals(activeReminders.size(), 1);
        assertEquals(completedReminders.size(), 2);
    }
}

