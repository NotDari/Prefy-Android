package com.example.prefy.Utils;

public class dateSinceSystem {
    private static final Double MONTH_IN_MILLIS = 2629800000.0, YEAR_IN_MILLIS = 31557600000.0, WEEK_IN_MILLIS = 604800016.56, DAY_IN_MILLIS = 86400000.0, HOUR_IN_MILLIS = 3600000.0, MINUTE_IN_MILLIS = 60000.0, SECONDS_IN_MILLIS = 60000.0;

    public static String getTimeSince(Double originalDate){
        Double currentDate = (double) System.currentTimeMillis();
        Double difference = currentDate - (originalDate* 1000);
        Integer result = 0;
        String outcome = "";
        if (difference <= SECONDS_IN_MILLIS){
            outcome = "Just now";
        } else if (difference > SECONDS_IN_MILLIS && difference <= MINUTE_IN_MILLIS){
            result = (int) Math.floor(difference/ SECONDS_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Second ago";
            } else {
                outcome = result + " Seconds ago";
            }
        } else if (difference > MINUTE_IN_MILLIS && difference <= HOUR_IN_MILLIS){
            result = (int) Math.floor(difference/ MINUTE_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Minute ago";
            } else {
                outcome = result + " Minutes ago";
            }
        } else if (difference > HOUR_IN_MILLIS && difference <= DAY_IN_MILLIS){
            result = (int) Math.floor(difference/ HOUR_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Hour ago";
            } else {
                outcome = result + " Hours ago";
            }
        } else if (difference > DAY_IN_MILLIS && difference <= WEEK_IN_MILLIS){
            result = (int) Math.floor(difference/ DAY_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Day ago";
            } else {
                outcome = result + " Days ago";
            }
        } else if (difference > WEEK_IN_MILLIS && difference <= MONTH_IN_MILLIS){
            result = (int) Math.floor(difference/ WEEK_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Weeks ago";
            } else {
                outcome = result + " Weeks ago";
            }
        } else if (difference > MONTH_IN_MILLIS && difference <= YEAR_IN_MILLIS){
            result = (int) Math.floor(difference/ MONTH_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Month ago";
            } else {
                outcome = result + " Months ago";
            }
        } else if (difference > YEAR_IN_MILLIS){
            result = (int) Math.floor(difference/ YEAR_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Year ago";
            } else {
                outcome = result + " Years ago";
            }
        }
        return outcome;
    }
}
