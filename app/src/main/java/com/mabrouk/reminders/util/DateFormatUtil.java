package com.mabrouk.reminders.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ahmad on 1/13/17.
 */

public class DateFormatUtil {
    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy MMM dd - kk : mm");
        return format.format(date);
    }

    private DateFormatUtil() {
        throw new IllegalStateException(getClass().getSimpleName() + " is a util class and shouldn't be instantiated");
    }
}
