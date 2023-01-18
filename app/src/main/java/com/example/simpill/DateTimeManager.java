/* (C) 2022 */
package com.example.simpill;

import android.app.AlarmManager;
import android.util.Log;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeManager {

    public final String DATE_TIME_FORMAT_WITH_TIMEZONE = "yyyy/MM/dd HH:mm Z";
    public final String DATE_FORMAT_WITH_TIMEZONE = "yyyy/MM/dd Z";
    public final String TIME_FORMAT_WITH_TIMEZONE = "HH:mm Z";
    public final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm";
    public final String DATE_FORMAT = "yyyy/MM/dd";
    public final String TIME_FORMAT = "HH:mm";
    public final String TIME_FORMAT_12_HRS = "h:mm a";

    public String getCurrentDateString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
        String currentDate = LocalDateTime.now().toDateTime().toString(dateTimeFormatter);
        Log.i("Joda", "Current Date: " + currentDate);
        return currentDate;
    }

    public int[] getCurrentCalendarDayMonthYear() {
        DateTime dateTime = LocalDateTime.now().toDateTime();
        return new int[] {
            dateTime.dayOfMonth().get(), dateTime.monthOfYear().get(), dateTime.year().get()
        };
    }

    public String addMonthToDateString(String date) {
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateFormatter);
        return localDateTime.plusMonths(1).toString(dateFormatter);
    }

    public String getCurrentDateAndTimeString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        String currentDate = LocalDateTime.now().toDateTime().toString(dateTimeFormatter);
        Log.i("Joda", "Current Date: " + currentDate);
        return currentDate;
    }

    public String getCurrentTimeString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
        String currentDate = LocalDateTime.now().toDateTime().toString(dateTimeFormatter);
        Log.i("Joda", "Current Date: " + currentDate);
        return currentDate;
    }

    public String formatLongAsDateString(long dateInMillis) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
        Instant instant = new Instant(dateInMillis);
        return instant.toString(dateTimeFormatter);
    }

    public String formatLongAsTimeString(long dateInMillis) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
        LocalDateTime localDateTime = new LocalDateTime(dateInMillis);
        return localDateTime.toString(dateTimeFormatter);
    }

    public String formatLongAsDateTimeString(long dateInMillis) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        LocalDateTime localDateTime = new LocalDateTime(dateInMillis);
        return localDateTime.toString(dateTimeFormatter);
    }

    public long formatDateTimeStringAsLong(String reminderTime) {
        return DateTime.parse(reminderTime, DateTimeFormat.forPattern(DATE_TIME_FORMAT))
                .withZone(DateTimeZone.getDefault())
                .getMillis();
    }

    public String convert12HrTimeTo24HrTime(String time) {
        return DateTimeFormat.forPattern(TIME_FORMAT_12_HRS)
                .withLocale(Locale.ENGLISH)
                .parseLocalTime(time)
                .toString(DateTimeFormat.forPattern(TIME_FORMAT));
    }

    public String convert24HrTimeTo12HrTime(String time) {
        return LocalTime.parse(time, DateTimeFormat.forPattern(TIME_FORMAT))
                .toString(DateTimeFormat.forPattern(TIME_FORMAT_12_HRS).withLocale(Locale.ENGLISH));
    }

    public String convertISODateStringToLocallyFormattedString(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormat.forPattern(DATE_FORMAT))
                .toString(DateTimeFormat.mediumDate());
    }

    public long convertTimeToCurrentDateTimeInMillis(String time) {
        LocalTime localTime = LocalTime.parse(time, DateTimeFormat.forPattern(TIME_FORMAT));
        LocalDateTime localDateTime =
                LocalDateTime.now(DateTimeZone.getDefault())
                        .withTime(
                                localTime.getHourOfDay(),
                                localTime.getMinuteOfHour(),
                                localTime.getSecondOfMinute(),
                                localTime.getMillisOfSecond());

        long reminderTime = localDateTime.toDateTime().getMillis();

        Instant reminderTimeInstant = new Instant(reminderTime);
        Instant currentTime = new Instant(System.currentTimeMillis());

        if (reminderTimeInstant.isBefore(currentTime)) {
            reminderTimeInstant = reminderTimeInstant.plus(AlarmManager.INTERVAL_DAY);
        }
        return reminderTimeInstant.getMillis();
    }

    public boolean isDateValid(String date) {
        try {
            DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(date);
            return true;
        } catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }
}
