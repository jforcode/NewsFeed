package com.jeevan.inshorts.util;

import java.util.Calendar;

/**
 * Created by jeevan on 9/14/17.
 */

public class Util {
    private static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July",
            "Aug", "Sept", "Oct", "Nov", "Dec"};

    public static String getDateDisplayString(Calendar cal) {
        return months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR);
    }

    public static String getDateDisplayString(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return getDateDisplayString(cal);
    }
}
