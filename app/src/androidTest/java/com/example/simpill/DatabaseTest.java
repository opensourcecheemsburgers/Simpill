/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.Pill.NULL_DB_ENTRY_STRING;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.sql.SQLDataException;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private static final Uri DEFAULT_ALARM_URI =
            Uri.parse("android.resource://com.winston69.simpill/" + R.raw.eas_alarm);

    public String getTestTime(int offset) {
<<<<<<< HEAD
        DateTimeManager dateTimeManager = new DateTimeManager();
        long oneMin = 60000L;
        return dateTimeManager.formatLongAsTimeString(
                System.currentTimeMillis() + (oneMin * offset));
=======
       DateTimeManager dateTimeManager = new DateTimeManager();
       long oneMin = 60000L;
       return dateTimeManager.formatLongAsTimeString(System.currentTimeMillis() + (oneMin * offset));
>>>>>>> 655fef7 (Simpill 1.3.1)
    }

    public Pill[] getTestPills() {
        Pill[] pills = new Pill[5];

        String[] pillNames = {"Melatonin", "Prozac", "Adderall", "Vitamins", "Supplements"};
        String[][] pillTimes = {
<<<<<<< HEAD
            new String[] {getTestTime(0)},
            new String[] {getTestTime(1), getTestTime(2)},
            new String[] {getTestTime(3), getTestTime(4)},
            new String[] {getTestTime(5), getTestTime(6), getTestTime(7)},
            new String[] {getTestTime(8), getTestTime(9), getTestTime(10), getTestTime(11)}
=======
                new String[]{getTestTime(0)},
                new String[]{getTestTime(1), getTestTime(2)},
                new String[]{getTestTime(3), getTestTime(4)},
                new String[]{getTestTime(5), getTestTime(6), getTestTime(7)},
                new String[]{getTestTime(8), getTestTime(9), getTestTime(10), getTestTime(11)}
>>>>>>> 655fef7 (Simpill 1.3.1)
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
    public void addPills() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        databaseHelper.deleteDatabase();

        Pill[] pills = getTestPills();
        for (Pill pill : pills) {
            pill.addToDatabase(context);
        }
    }

    @Test
<<<<<<< HEAD
    public void retrieveAndVerifyPills() {
=======
    public void retrieveAndVerifyPills() throws SQLDataException {
>>>>>>> 655fef7 (Simpill 1.3.1)
        addPills();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Pill[] pills = getTestPills();
        Pill[] retrievedPills = databaseHelper.getAllPills();

        for (int index = 0; index < pills.length; index++) {
            Pill pill = pills[index];
            Pill retrievedPill = retrievedPills[index];
<<<<<<< HEAD
            assert arePillsEqual(pill, retrievedPill);
=======

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
            boolean alarmsSet = retrievedPill.getAlarmsSet() == pill.getAlarmsSet();
            boolean bottleColor = retrievedPill.getBottleColor() == pill.getBottleColor();

            String eq = "equal";
            String notEq = "not equal";

            System.out.println("Name " + (name ? eq : notEq));
            System.out.println("Times " + (times ? eq : notEq));
            System.out.println("TimeTaken " + (timeTaken ? eq : notEq));
            System.out.println("StartDate " + (startDate ? eq : notEq));
            System.out.println("StockupDate " + (stockupDate ? eq : notEq));
            System.out.println("Uri " + (customAlarmUri ? eq : notEq));
            System.out.println("AlarmType " + (alarmType ? eq : notEq));
            System.out.println("AlarmsSet " + (alarmsSet ? eq : notEq));
            System.out.println("BottleColor " + (bottleColor ? eq : notEq));
            

            if (!name
                    || !times
                    || !timeTaken
                    || !startDate
                    || !stockupDate
                    || !customAlarmUri
                    || !alarmType
                    || !alarmsSet
                    || !bottleColor) throw new SQLDataException();

            System.out.println("");
>>>>>>> 655fef7 (Simpill 1.3.1)
        }
    }

    @Test
    public void readPillsCursor() {
        addPills();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Cursor cursor = databaseHelper.readSqlDatabase();
        cursor.moveToFirst();
    }

    @Test
    public void retrievePillsByPrimaryKey() {
        addPills();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
<<<<<<< HEAD
        Pill[] pills = databaseHelper.getAllPills();

        for (Pill pill : pills) {
            Pill retrievedPill = databaseHelper.getPill(pill.getPrimaryKey());
            assert arePillsEqual(pill, retrievedPill);
        }
    }

    public boolean arePillsEqual(Pill firstPill, Pill secondPill) {
        boolean name = firstPill.getName().equals(secondPill.getName());
        boolean times = true;
        String[] pillTimesArray = secondPill.getTimesArray();
        String[] retrievedPillTimesArray = firstPill.getTimesArray();
        for (int timeIndex = 0; timeIndex < retrievedPillTimesArray.length; timeIndex++) {
            if (!retrievedPillTimesArray[timeIndex].equals(pillTimesArray[timeIndex])) {
                times = false;
                break;
            }
        }
        boolean timeTaken = firstPill.getTimeTaken().equals(secondPill.getTimeTaken());
        boolean startDate = firstPill.getStartDate().equals(secondPill.getStartDate());
        boolean stockupDate = firstPill.getStockupDate().equals(secondPill.getStockupDate());
        boolean customAlarmUri =
                secondPill.getCustomAlarmUri()
                        .toString()
                        .equals(firstPill.getCustomAlarmUri().toString());
        boolean alarmType = firstPill.getAlarmType() == secondPill.getAlarmType();
        boolean bottleColor = firstPill.getBottleColor() == secondPill.getBottleColor();


        String eq = "equal";
        String notEq = "not equal";

        System.out.println("Name " + (name ? eq : notEq));
        System.out.println("Times " + (times ? eq : notEq));
        System.out.println("TimeTaken " + (timeTaken ? eq : notEq));
        System.out.println("StartDate " + (startDate ? eq : notEq));
        System.out.println("StockupDate " + (stockupDate ? eq : notEq));
        System.out.println("Uri " + (customAlarmUri ? eq : notEq));
        System.out.println("AlarmType " + (alarmType ? eq : notEq));
        System.out.println("BottleColor " + (bottleColor ? eq : notEq));

        return name
                && times
                && timeTaken
                && startDate
                && stockupDate
                && customAlarmUri
                && alarmType
                && bottleColor;
    }

=======
        Pill[] retrievedPills = databaseHelper.getAllPills();

        for (Pill retrievedPill : retrievedPills) {
            databaseHelper.getPill(retrievedPill.getPrimaryKey());
        }
    }
>>>>>>> 655fef7 (Simpill 1.3.1)
}
