package com.daribear.prefy.Utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Helper class that retrieves the current time.
 */
public class CurrentTime {

    /**
     * Receives the current time in GMT.
     * @return the current time
     */
    public static long getCurrentTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return cal.getTimeInMillis();
    }
}
