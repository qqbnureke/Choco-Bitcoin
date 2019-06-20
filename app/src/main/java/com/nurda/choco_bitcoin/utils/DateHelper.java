package com.nurda.choco_bitcoin.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHelper {
    private static long now = System.currentTimeMillis();
    private static long oneDay = 1000L * 60 * 60 * 24;

    private static String millisecondsToDate(long milliseconds){
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

    public static String getCurrentTime(){
        return millisecondsToDate(now);
    }

    public static String getLastWeek(){
        long oneWeek = now - oneDay * 7;
        return millisecondsToDate(oneWeek);
    }

    public static String getLastMonth(){
        long oneMonth = now - oneDay * 28;
        return millisecondsToDate(oneMonth);
    }

    public static String getLastYear(){
        long oneYear = now - oneDay * 365;
        return millisecondsToDate(oneYear);
    }
}
