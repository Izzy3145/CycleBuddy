package com.example.android.cyclebuddy.helpers;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String displayTimeOrDate(long milliseconds){
        if(DateUtils.isToday(milliseconds)){
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(milliseconds);
            String timestamp = dateFormat.format(date);
            return timestamp;
        } else {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(milliseconds);
            String date = DateFormat.format("dd-MM-yyyy", cal).toString();
            return date;
        }
    }

    //if date of message is today, show time
    public static String getCurrentTime() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    Date date = new Date();
    String timestamp = dateFormat.format(date);
    return timestamp;
    }

    //if date of message is before today, show date
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }


}
