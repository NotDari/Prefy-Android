package com.daribear.prefy.Utils.GeneralUtils;

import com.daribear.prefy.Utils.CurrentTime;

/**
 * This class is used to get the date since a time.
 * This is used in posts/ comments to display the time since it was posted e.g. (posted 10 seconds ago)
 */
public class dateSinceSystem {
    //Time in milliseconds
    private static final Double MONTH_IN_MILLIS = 2629800000.0, YEAR_IN_MILLIS = 31557600000.0, WEEK_IN_MILLIS = 604800016.56, DAY_IN_MILLIS = 86400000.0, HOUR_IN_MILLIS = 3600000.0, MINUTE_IN_MILLIS = 60000.0, SECONDS_IN_MILLIS = 60000.0;

    /**
     * Gets a double of the date in milis and returns a string containing the details of the time since that timestamp.
     * For example 10 seconds ago
     * @param originalDate timestamp to get how long ago it was
     * @return string containing how long ago the timestamp was
     */
    public static String getTimeSince(Double originalDate){
        Double currentDate = (double) CurrentTime.getCurrentTime();
        Double difference = currentDate - (originalDate* 1000);
        Integer result = 0;
        String outcome = "";
        //Check if its les than a second ago
        if (difference <= SECONDS_IN_MILLIS){
            outcome = "Just now";
        } //Check if its less than a minute ago
        else if (difference <= MINUTE_IN_MILLIS){
            result = (int) Math.floor(difference/ SECONDS_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Second ago";
            } else {
                outcome = result + " Seconds ago";
            }
        }
        //Check if its less than a hour ago
        else if (difference <= HOUR_IN_MILLIS){
            result = (int) Math.floor(difference/ MINUTE_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Minute ago";
            } else {
                outcome = result + " Minutes ago";
            }
        }
        //Check if its less than a day ago
        else if (difference <= DAY_IN_MILLIS){
            result = (int) Math.floor(difference/ HOUR_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Hour ago";
            } else {
                outcome = result + " Hours ago";
            }
        }
        //Check if its less than a week ago
        else if (difference <= WEEK_IN_MILLIS){
            result = (int) Math.floor(difference/ DAY_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Day ago";
            } else {
                outcome = result + " Days ago";
            }
        }
        //Check if its less than a month ago
        else if (difference <= MONTH_IN_MILLIS){
            result = (int) Math.floor(difference/ WEEK_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Week ago";
            } else {
                outcome = result + " Weeks ago";
            }
        }
        //Check if its less than a year ago
        else if (difference <= YEAR_IN_MILLIS){
            result = (int) Math.floor(difference/ MONTH_IN_MILLIS);
            if (result == 1) {
                outcome = result + " Month ago";
            } else {
                outcome = result + " Months ago";
            }
        }
        //Its more than a year so get the number of years
        else {
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
