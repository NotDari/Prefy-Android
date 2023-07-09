package com.daribear.prefy.Utils;

import java.util.Calendar;
import java.util.TimeZone;

public class CurrentTime {

    public static long getCurrentTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return cal.getTimeInMillis();
    }
}
