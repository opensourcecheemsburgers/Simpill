package com.example.simpill;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeManager {

    public TimeZone getUserTimezone() {
        return TimeZone.getDefault();
    }

    public String getCurrentDate(Context ct, TimeZone userTimeZone) {
        Calendar calendar = GregorianCalendar.getInstance(userTimeZone);
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(ct.getString(R.string.date_format_with_timezone));
        return dateFormat.format(date);
    }

    public String getCurrentDateAndTime(Context ct, TimeZone userTimeZone) {
        Calendar calendar = GregorianCalendar.getInstance(userTimeZone);
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(ct.getString(R.string.full_date_time_format));
        return dateFormat.format(date);
    }

    public String getCurrentTime(Context ct, TimeZone userTimeZone) {
        Calendar calendar = GregorianCalendar.getInstance(userTimeZone);
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(ct.getString(R.string.time_format_24hr));
        return dateFormat.format(date);
    }

    public String formatDateAsString(Context ct, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ct.getString(R.string.full_date_time_format), Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public String formatLongAsDateString(Context ct, long dateInMillis) {
        Date date = new Date();
        date.setTime(dateInMillis);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ct.getString(R.string.full_date_time_format));
        return simpleDateFormat.format(date);
    }

    public String formatLongAsTimeString(Context ct, long dateInMillis) {
        Date date = new Date();
        date.setTime(dateInMillis);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ct.getString(R.string.time_format_24hr));
        return simpleDateFormat.format(date);
    }

    public String convert12HrTimeTo24HrTime(Context ct, String timeIn12HrFormat) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(ct.getString(R.string.time_format_12hr), Locale.US);
        Date date = null;

        try {
            date = dateFormat.parse(timeIn12HrFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        @SuppressLint("SimpleDateFormat") DateFormat dateFormat1 = new SimpleDateFormat(ct.getString(R.string.time_format_24hr));

        if (date != null) {
            return dateFormat1.format(date);
        } else {
            throw new NullPointerException();
        }
    }

    public String convert24HrTimeTo12HrTime(Context ct, String timeIn24HrFormat) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(ct.getString(R.string.time_format_24hr));
        Date date = null;

        try {
            date = dateFormat.parse(timeIn24HrFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat1 = new SimpleDateFormat(ct.getString(R.string.time_format_12hr), Locale.US);

        if (date != null) {
            return dateFormat1.format(date);
        } else {
            throw new NullPointerException();
        }
    }

    public String convert24HrArrayTo12HrStrings(Context context, String[] timeArray) {
        DatabaseHelper myDatabase = new DatabaseHelper(context);
        for (int currentArrayIndex = 0; currentArrayIndex < timeArray.length; currentArrayIndex++) {
            timeArray[currentArrayIndex] = convert24HrTimeTo12HrTime(context, timeArray[currentArrayIndex]);
        }

        String s = myDatabase.convertArrayToString(timeArray);

        System.out.println(s);

        return s;
    }

    public Calendar formatDateStringAsCalendar(Context ct, TimeZone userTimezone, String dateString) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ct.getString(R.string.date_format));

        Calendar calendar = Calendar.getInstance(userTimezone);

        Date date = Calendar.getInstance(userTimezone).getTime();
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.setTimeZone(userTimezone);


            return calendar;
        }
        else {
            throw new NullPointerException();
        }
    }
    public Calendar formatTimeStringAsCalendar(Context ct, TimeZone userTimezone, String timeString) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ct.getString(R.string.time_format_24hr));

        int currentYear = Calendar.getInstance(userTimezone).get(Calendar.YEAR);
        int currentDayOfYear = Calendar.getInstance(userTimezone).get(Calendar.DAY_OF_YEAR);

        Calendar calendar = Calendar.getInstance(userTimezone);

        Date date = Calendar.getInstance(userTimezone).getTime();
        try {
            date = simpleDateFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            calendar.setTime(date);
            calendar.set(Calendar.YEAR, currentYear);
            calendar.set(Calendar.DAY_OF_YEAR, currentDayOfYear);

            return calendar;
        }
        else {
            throw new NullPointerException();
        }
    }

}
