package com.nurda.choco_bitcoin.utils;

import android.annotation.SuppressLint;

import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateHelper {
    private static long now = System.currentTimeMillis();
    private static long oneDay = 1000L * 60 * 60 * 24;

    public static String millisecondsToDate(long milliseconds){
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

    public static String millisecondsToTime(long milliseconds){
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm");
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

    public static String[] getWeekNames(){
        String [] weekNames = new String[7];
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -8);

        for(int i = 0; i< 7; i++){
            cal.add(Calendar.DAY_OF_WEEK, 1);
            weekNames[i] = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        };

        return weekNames;
    }

    public static String[] getMonthNames(){
        String [] monthNames = new String[12];
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -12);

        for (int i=0;i<12;i++){
            cal.add(Calendar.MONTH, 1);
            monthNames[i] = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        }

        return monthNames;

    }

}
