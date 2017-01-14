package com.mabrouk.reminders.util;

/**
 * Created by ahmad on 1/13/17.
 */

public class Constants {
    public static final int GEOFENCE_RADIUS = 100;
    private Constants() {
        throw new IllegalStateException(getClass().getSimpleName() + " is a util class and shouldn't be instantiated");
    }
}
