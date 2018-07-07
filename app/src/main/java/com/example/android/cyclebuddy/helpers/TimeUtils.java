package com.example.android.cyclebuddy.helpers;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String displayTimeOrDate(long milliseconds) {
        if (DateUtils.isToday(milliseconds)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(milliseconds);
            String timestamp = dateFormat.format(date);
            return timestamp;
        } else {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(milliseconds);
            String date = DateFormat.format("dd/MM/yyyy", cal).toString();
            return date;
        }
    }

}
