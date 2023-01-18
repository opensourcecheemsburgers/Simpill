/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.Pill.NULL_DB_ENTRY_STRING;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.sql.SQLDataException;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private static final Uri DEFAULT_ALARM_URI =
            Uri.parse("android.resource://com.winston69.simpill/" + R.raw.eas_alarm);

    public Pill[] getTestPills() {
        Pill[] pills = new Pill[5];

        int[] primaryKeys = new int[] {1, 2, 3, 4, 5};
        String[] pillNames = {"Melatonin", "Prozac", "Adderall", "Vitamins", "Supplements"};
        String[][] pillTimes = {
            new String[] {"00:00"},
            new String[] {"08:00", "16:00"},
            new String[] {"09:00", "15:00"},
            new String[] {"08:00", "13:00", "17:00"},
            new String[] {"08:00", "13:00", "17:00", "21:00"}
        };
        String[] pillStockups = {
            "2023/01/19", "2023/01/19", "2023/01/19", "2023/01/19", "2023/01/19"
        };
        Uri[] customAlarmUris = {
            DEFAULT_ALARM_URI,
            DEFAULT_ALARM_URI,
            DEFAULT_ALARM_URI,
            DEFAULT_ALARM_URI,
            DEFAULT_ALARM_URI
        };
        String[] pillStartDates = {
            NULL_DB_ENTRY_STRING,
            NULL_DB_ENTRY_STRING,
            NULL_DB_ENTRY_STRING,
            NULL_DB_ENTRY_STRING,
            NULL_DB_ENTRY_STRING
        };
        int[] pillSupplies = {30, 30, 30, 30, 30};
        int[] pillFrequency = {1, 0, 0, 0, 0};
        int[] isTaken = {0, 0, 0, 0, 0};
        int[] alarmTypes = {
            DatabaseHelper.ALARM,
            DatabaseHelper.ALARM,
            DatabaseHelper.ALARM,
            DatabaseHelper.ALARM,
            DatabaseHelper.ALARM
        };
        String[] pillTakenTimes = {
            NULL_DB_ENTRY_STRING,
            NULL_DB_ENTRY_STRING,
            NULL_DB_ENTRY_STRING,
            NULL_DB_ENTRY_STRING,
            NULL_DB_ENTRY_STRING
        };
        int[] alarmsSetArr = {0, 0, 0, 0, 0};
        int[] bottleColorArr = {2, 2, 2, 2, 2};

        for (int index = 0; index < pills.length; index++) {
            pills[index] =
                    new Pill(
                            primaryKeys[index],
                            pillNames[index],
                            pillTimes[index],
                            pillStartDates[index],
                            pillStockups[index],
                            customAlarmUris[index],
                            pillFrequency[index],
                            isTaken[index],
                            pillTakenTimes[index],
                            pillSupplies[index],
                            alarmTypes[index],
                            alarmsSetArr[index],
                            bottleColorArr[index]);
        }
        return pills;
    }

    @Test
    public void addAndRetrievePill() throws SQLDataException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        databaseHelper.deleteDatabase();

        Pill[] pills = getTestPills();
        for (Pill pill : pills) {
            pill.addToDatabase(context);
        }

        Pill[] retrievedPills = databaseHelper.getAllPills();

        for (int index = 0; index < pills.length; index++) {
            Pill pill = pills[index];
            Pill retrievedPill = retrievedPills[index];

            boolean name = retrievedPill.getName().equals(pill.getName());
            boolean times = true;
            String[] pillTimesArray = pill.getTimesArray();
            String[] retrievedPillTimesArray = retrievedPill.getTimesArray();
            for (int timeIndex = 0; timeIndex < retrievedPillTimesArray.length; timeIndex++) {
                if (!retrievedPillTimesArray[timeIndex].equals(pillTimesArray[timeIndex])) {
                    times = false;
                    break;
                }
            }
            boolean timeTaken = retrievedPill.getTimeTaken().equals(pill.getTimeTaken());
            boolean startDate = retrievedPill.getStartDate().equals(pill.getStartDate());
            boolean stockupDate = retrievedPill.getStockupDate().equals(pill.getStockupDate());
            boolean customAlarmUri =
                    pill.getCustomAlarmUri()
                            .toString()
                            .equals(retrievedPill.getCustomAlarmUri().toString());
            boolean alarmType = retrievedPill.getAlarmType() == pill.getAlarmType();
            boolean alarmsSet =
                    retrievedPill.getAlarmsSet()
                            != pill.getAlarmsSet(); // this gets changed because alarms are set
            // after being added to db.
            boolean bottleColor = retrievedPill.getBottleColor() == pill.getBottleColor();

            if (!name
                    || !times
                    || !timeTaken
                    || !startDate
                    || !stockupDate
                    || !customAlarmUri
                    || !alarmType
                    || !alarmsSet
                    || !bottleColor) throw new SQLDataException();
        }
    }

    @Test
    public void readPillsCursor() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Cursor cursor = databaseHelper.readSqlDatabase();
        cursor.moveToFirst();
    }
}
