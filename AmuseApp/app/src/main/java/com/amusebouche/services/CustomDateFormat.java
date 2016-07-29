package com.amusebouche.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Custom date format class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com>
 *
 * This class provides methods to manipulate dates.
 */
public class CustomDateFormat {
    private static final String API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String HOUR_FORMAT = "HH:mm";

    public static String getUTCString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static Date getUTCDate(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date parse = new Date();
        try {
            parse = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parse;
    }

    public static String getDateTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(HOUR_FORMAT, Locale.getDefault());
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date) + " " + sdf.format(date);
    }
}
