package com.mabrouk.reminders.model;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by ahmad on 1/14/17.
 */

public class ReminderValidationTest {

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTime() {
        Reminder.createTimeReminder(0, "", -1, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLat() {
        Reminder.createLocationReminder(0, "", 92, 0, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLong() {
        Reminder.createLocationReminder(0, "", 0, 200, false);
    }

    @Test
    public void testValidTime() {
        long timestamp = System.currentTimeMillis();
        Reminder reminder = Reminder.createTimeReminder(0, "", timestamp, false);
        assertThat(reminder.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    public void testValidLocation() {
        double lat = 12.05;
        double lon = -23.0;
        Reminder reminder = Reminder.createLocationReminder(0, "", lat, lon, false);
        assertThat(reminder.getLatitude()).isEqualTo(lat, Offset.offset(0.00000001));
        assertThat(reminder.getLongitude()).isEqualTo(lon, Offset.offset(0.0000001));
    }
}
