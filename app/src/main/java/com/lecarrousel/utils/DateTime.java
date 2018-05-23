package com.lecarrousel.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTime {

    public static final String DATE_FORMAT_FOR_SERVER = "yyyy-MM-dd HH:mm:ss";
    //    public static final String DATE_FORMAT = "EEE, MMM d, yyyy";
    public static final String DATE_FORMAT = "EEE, MMM d, yyyy ";
    public static final String INPUT_DATE_FORMAT = "EEE, MMM d, yyyy hh:mm a";
    public static final String LOCAL_DATE = "dd-MMM-yyyy";

    private Date mDate;
    private Calendar mCalendar;


    public DateTime() {

        this(new Date());

    }


    public DateTime(Date date) {

        mDate = date;
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(mDate);

    }


    public DateTime(String dateFormat, String dateString) {

        mCalendar = Calendar.getInstance();
        SimpleDateFormat mFormat = new SimpleDateFormat(dateFormat);

        try {
            mDate = mFormat.parse(dateString);
            mCalendar.setTime(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public DateTime(String dateString) {

        this(DATE_FORMAT, dateString);

    }


    public DateTime(int year, int monthOfYear, int dayOfMonth,
                    int hourOfDay, int minuteOfHour, int secondOfMinute) {

        mCalendar = Calendar.getInstance();
        mCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute);
        mDate = mCalendar.getTime();

    }


    public DateTime(int year, int monthOfYear, int dayOfMonth,
                    int hourOfDay, int minuteOfHour) {

        this(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, 0);

    }


    public DateTime(int year, int monthOfYear, int dayOfMonth) {

        this(year, monthOfYear, dayOfMonth, 0, 0, 0);

    }

    public Date getDate() {
        return mDate;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public String getDateString(String dateFormat) {

        SimpleDateFormat mFormat = new SimpleDateFormat(dateFormat);
        return mFormat.format(mDate);

    }

    public String getDateString() {

        return getDateString(DATE_FORMAT);

    }

    public int getYear() {

        return mCalendar.get(Calendar.YEAR);

    }

    public int getMonthOfYear() {

        return mCalendar.get(Calendar.MONTH);

    }

    public int getDayOfMonth() {

        return mCalendar.get(Calendar.DAY_OF_MONTH);

    }

    public int getHourOfDay() {

        return mCalendar.get(Calendar.HOUR_OF_DAY);

    }

    public int getMinuteOfHour() {

        return mCalendar.get(Calendar.MINUTE);

    }

    public int getSecondOfMinute() {

        return mCalendar.get(Calendar.SECOND);

    }

    public static String getparseDateToddMMyyyy(String indate) {
        Log.e("inDate-->", "" + indate);
        SimpleDateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_FORMAT);
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_FORMAT_FOR_SERVER);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(indate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("inDate-->str", "" + str);
        return str;
    }

    // From Divyesh
    public static String getFormatedDate(String dateString, String outputformat, String inputFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        try {
            Date date = sdf.parse(dateString);
            String formated = new SimpleDateFormat(outputformat).format(date);
            return formated;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getFormatedDateInMili(String dateString, String inputFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        try {
            Date date = sdf.parse(dateString);
            return  date.getTime();

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    //for Samsung API
    public static String convertDateFormate(String indate, SimpleDateFormat inputFormat, SimpleDateFormat outputFormat) {
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(indate);
            str = outputFormat.format(date);
            return str;

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getTimeDeference(Date oldDateTime) {
        // TODO Auto-generated method stub

        int day = 0;
        int hh = 0;
        int mm = 0;
        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date oldDate = dateFormat.parse(oldDateTime);
            Date cDate = new Date();

            Log.e("cDate.getTime()","--"+cDate.getTime());
            Log.e("oldDateTime.getTime()","--"+oldDateTime.getTime());
            Long timeDiff = cDate.getTime() - oldDateTime.getTime();
            day = (int) TimeUnit.MILLISECONDS.toDays(timeDiff);
            hh = (int) (TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(day));
            mm = (int) (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
        } catch (android.net.ParseException e) {
            e.printStackTrace();
        }

        return mm;
    }
}