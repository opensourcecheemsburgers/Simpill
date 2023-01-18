/* (C) 2023 */
package com.example.simpill;

import android.app.AlarmManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DateTimeTests {

    private final String DATE_TIME_FORMAT_WITH_TIMEZONE = "yyyy/MM/dd HH:mm Z";
    private final String DATE_FORMAT_WITH_TIMEZONE = "yyyy/MM/dd Z";
    private final String TIME_FORMAT_WITH_TIMEZONE = "HH:mm Z";

    private final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm";
    private final String DATE_FORMAT = "yyyy/MM/dd";
    private final String TIME_FORMAT = "H:mm";
    private final String TIME_FORMAT_12_HRS = "hh:mm a";

    @Test
    public void getCurrentDate() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT_WITH_TIMEZONE);
        String currentDate = LocalDateTime.now().toDateTime().toString(dateTimeFormatter);
        System.out.println("Current Date: " + currentDate);
    }

    @Test
    public void addMonthToDate() {
        String date = "2022/10/1";
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        System.out.println(localDateTime.plusMonths(1).toString(dateTimeFormatter));
    }

    @Test
    public void getCurrentDateAndTime() {
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormat.forPattern(DATE_TIME_FORMAT_WITH_TIMEZONE);
        String currentDate = LocalDateTime.now().toDateTime().toString(dateTimeFormatter);
        System.out.println("Current Date: " + currentDate);
    }

    @Test
    public void getCurrentTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(TIME_FORMAT_WITH_TIMEZONE);
        String currentDate = LocalDateTime.now().toDateTime().toString(dateTimeFormatter);
        System.out.println("Current Date: " + currentDate);
    }

    @Test
    public void getCurrentCalendarDayMonthYear() {
        DateTime dateTime = LocalDateTime.now().toDateTime();
        int[] arr =
                new int[] {
                    dateTime.dayOfMonth().get(), dateTime.monthOfYear().get(), dateTime.year().get()
                };
    }

    @Test
    public void formatTimeStringAsLong() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        DateTimeFormatter fullDateTimeFormatter =
                DateTimeFormat.forPattern(DATE_TIME_FORMAT_WITH_TIMEZONE);
        String time = "2022/11/04 18:00";
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);

        long localDateTimeLong = localDateTime.toDateTime().getMillis();
    }

    @Test
    public void convert12HrTimeTo24HrTime() {
        System.out.println(
                "12hr format: "
                        + DateTimeFormat.forPattern(TIME_FORMAT_12_HRS)
                                .withLocale(Locale.ENGLISH)
                                .parseLocalTime("6:00 PM")
                                .toString(DateTimeFormat.forPattern(TIME_FORMAT)));
    }

    @Test
    public void convert24HrTimeTo12HrTime() {
        String time = "18:00";
        LocalTime localTime = LocalTime.parse(time, DateTimeFormat.forPattern(TIME_FORMAT));

        System.out.println(
                "24hr format: "
                        + localTime.toString(
                                DateTimeFormat.forPattern(TIME_FORMAT_12_HRS)
                                        .withLocale(Locale.ENGLISH)));
    }

    @Test
    public void convertTimeToCurrentDateWithTime() {
        String time = "16:00";

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

        System.out.println(
                "Time formatted to current date and with set time: "
                        + localDateTime.toString(DATE_TIME_FORMAT_WITH_TIMEZONE));
        System.out.println(
                "Time formatted to current date and with set time in ms: "
                        + reminderTimeInstant
                                .toDateTime()
                                .toString(DATE_TIME_FORMAT_WITH_TIMEZONE));
    }

    @Test
    public void isDateValid() {
        String[] dates =
                new String[] {
                    "2022/12/26",
                    "2022/12/27",
                    "2022/12/28",
                    "2022/12/29",
                    "2022/12/30",
                    "2022/12/31",
                    "2023/1/1",
                    "",
                    "null",
                    "wwwwww"
                };
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
        for (String date : dates) {
            try {
                dateTimeFormatter.parseDateTime(date);
            } catch (IllegalArgumentException illegalArgumentException) {
                System.out.println("Can't format date: " + date);
            }
        }
    }
}
