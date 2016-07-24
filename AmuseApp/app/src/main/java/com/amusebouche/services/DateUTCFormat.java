package com.amusebouche.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by nessa on 24/07/16.
 */
public class DateUTCFormat {
    private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getUTCString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
}
