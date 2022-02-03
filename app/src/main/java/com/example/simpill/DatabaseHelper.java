package com.example.simpill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PillList.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "PillList";

    private static final String COLUMN_PK = "PrimaryKey";
    public static final String COLUMN_TITLE = "PillName";
    private static final String COLUMN_TIME = "PillTime";
    private static final String COLUMN_FREQUENCY = "PillFrequency";
    private static final String COLUMN_STOCKUP = "PillStockup";
    private static final String COLUMN_SUPPLY = "PillSupply";
    private static final String COLUMN_ISTAKEN = "IsPillTaken";
    private static final String COLUMN_TIMETAKEN = "TimeTaken";
    private static final String COLUMN_ALARMSSET = "AlarmsSet";
    private static final String COLUMN_BOTTLECOLOR = "BottleColor";

    public static final int MULTIPLE_DAILY = 0;
    public static final int DAILY = 1;
    public static final int EVERY_OTHER_DAY = 2;
    public static final int WEEKLY = 7;

    private static final String SELECTION = "PillName = ?";

    public static String strSeparator = ", ";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +

                " (" + COLUMN_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_FREQUENCY + " INTEGER, " +
                COLUMN_STOCKUP + " TEXT, " +
                COLUMN_SUPPLY + " INTEGER, " +
                COLUMN_ISTAKEN + " INTEGER, " +
                COLUMN_TIMETAKEN + " TEXT, " +
                COLUMN_ALARMSSET + " INTEGER, " +
                COLUMN_BOTTLECOLOR + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDbNumber, int newDbNumber) {
        if (newDbNumber > oldDbNumber) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_FREQUENCY + " INTEGER DEFAULT 1");
        }
    }

    public int getRowCount() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase myDatabase = this.getReadableDatabase();
        int length = 0;

        Cursor cursor;
        if (myDatabase != null) {
            cursor = myDatabase.rawQuery(query, null);
            length = cursor.getCount();
            cursor.close();
        }
        return length;
    }

    public Cursor readSqlDatabase() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase myDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        if (myDatabase != null) {
            cursor = myDatabase.rawQuery(query, null);
        }
        return cursor;
    }

    public boolean addNewPill(int id, String title, String[] time, int frequency, String stockup, int supply, int isTaken, String takenTime, int alarmsSet, int bottleColor) {
        SQLiteDatabase pillDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        insertAllContentValues(cv, id, title, time, frequency, stockup, supply, isTaken, takenTime, alarmsSet, bottleColor);
        long result = pillDatabase.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    public boolean updatePill(String pillName, String newPillName, String[] time, int frequency, String stockup, int supply, int isTaken, String takenTime, int alarmsSet, int bottleColor) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();

        removeAllContentValuesExceptPk(cv);
        insertAllContentValuesExceptPk(cv, newPillName, time, frequency, stockup, supply, isTaken, takenTime, alarmsSet, bottleColor);

        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);

        cursor.close();
        return true;
    }
    public Boolean deletePill(String pillName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = new String[]{(pillName)};

        long result = db.delete(TABLE_NAME, SELECTION, selectionArgs);
        return result != -1;
    }

    private void insertAllContentValues(ContentValues cv, int primaryKey, String title, String[] time, int frequency, String stockup, int supply, int isTaken, String takenTime, int alarmsSet, int bottleColor) {
        cv.put(COLUMN_PK, primaryKey);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TIME, convertArrayToString(time));
        cv.put(COLUMN_FREQUENCY, frequency);
        cv.put(COLUMN_STOCKUP, stockup);
        cv.put(COLUMN_SUPPLY, supply);
        cv.put(COLUMN_ISTAKEN, isTaken);
        cv.put(COLUMN_TIMETAKEN, takenTime);
        cv.put(COLUMN_ALARMSSET, alarmsSet);
        cv.put(COLUMN_BOTTLECOLOR, bottleColor);
    }
    private void insertAllContentValuesExceptPk(ContentValues cv, String title, String[] time, int frequency, String stockup, int supply, int isTaken, String takenTime, int alarmsSet, int bottleColor) {
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TIME, convertArrayToString(time));
        cv.put(COLUMN_FREQUENCY, frequency);
        cv.put(COLUMN_STOCKUP, stockup);
        cv.put(COLUMN_SUPPLY, supply);
        cv.put(COLUMN_ISTAKEN, isTaken);
        cv.put(COLUMN_TIMETAKEN, takenTime);
        cv.put(COLUMN_ALARMSSET, alarmsSet);
        cv.put(COLUMN_BOTTLECOLOR, bottleColor);
    }
    private void removeAllContentValuesExceptPk(ContentValues cv) {
        cv.remove(COLUMN_TITLE);
        cv.remove(COLUMN_TIME);
        cv.remove(COLUMN_FREQUENCY);
        cv.remove(COLUMN_STOCKUP);
        cv.remove(COLUMN_SUPPLY);
        cv.remove(COLUMN_ISTAKEN);
        cv.remove(COLUMN_TIMETAKEN);
        cv.remove(COLUMN_ALARMSSET);
        cv.remove(COLUMN_BOTTLECOLOR);
    }

    public Boolean deleteDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, null, null);
        return result != -1;
    }

    public String getPillName(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (cursor != null && cursor.moveToFirst()) {
            String userPillName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            cursor.close();
            return userPillName;
        }
        else {
            return "null";
        }
    }
    public void setPillName(String pillName, String newPillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_TITLE);
        cv.put(COLUMN_TITLE, newPillName);
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();
    }

    public String[] getPillTime(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            String[] pillTime = convertStringToArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
            cursor.close();
            return pillTime;
        } else {
            throw new SQLiteException();
        }
    }
    public void setPillTime(String pillName, String[] newPillTime) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_TIME);
        cv.put(COLUMN_TIME, convertArrayToString(newPillTime));
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();
    }

    public int getFrequency(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            int frequency = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY));
            cursor.close();
            return frequency;
        } else {
            throw new SQLiteException();
        }
    }
    public void setFrequency(String pillName, int frequency) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_TIME);
        cv.put(COLUMN_TIME, frequency);
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();
    }

    public int getPillAmount(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            int pillSupplyInSqlDatabase = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUPPLY));
            cursor.close();
            return pillSupplyInSqlDatabase;
        } else {
            throw new SQLiteException();
            //return -1;
        }

    }
    public void setPillAmount(String pillName, int newPillSupply) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_SUPPLY);
        cv.put(COLUMN_SUPPLY, newPillSupply);
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();
    }

    public String getPillDate(String pillName) {
        System.out.println("Pill Name passed is: " + pillName);


        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            String stockup = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOCKUP));
            cursor.close();
            return stockup;
        } else {
            throw new SQLiteException();
            //return "PillDBHelper cannot fetch Pill Date";
        }
    }
    public void setPillDate(String pillName, String newPillDate) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_STOCKUP);
        cv.put(COLUMN_STOCKUP, newPillDate);
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();
    }

    public String getTimeTaken(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToNext()) {
            String TimeTakenInSqlDatabase = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMETAKEN));
            cursor.close();
            return TimeTakenInSqlDatabase;
        } else {
            throw new SQLiteException();
            //return "There is no data in the SQLite database.";
        }
    }
    public void setTimeTaken(String pillName, String currentTime) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_TIMETAKEN);
        cv.put(COLUMN_TIMETAKEN, currentTime);
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();
    }

    public int getIsTaken(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            int isTakenInSqlDatabase = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ISTAKEN));
            cursor.close();
            return isTakenInSqlDatabase;
        } else {
            throw new SQLiteException();
            //return -1;
        }
    }
    public void setIsTaken(String pillName, int isTakenValue) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_ISTAKEN);
        cv.put(COLUMN_ISTAKEN, isTakenValue);
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();

    }

    public boolean getIsReminderSet(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            int isTakenInSqlDatabase = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALARMSSET));
            cursor.close();
            return isTakenInSqlDatabase == 1;
        } else {
            throw new SQLiteException();
            //return -1;
        }
    }
    public void setIsReminderSet(String pillName, int alarmsSetValue) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_ALARMSSET);
        cv.put(COLUMN_ALARMSSET, alarmsSetValue);
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();
    }

    public int getBottleColor(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            int bottleColor = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOTTLECOLOR));
            cursor.close();
            return bottleColor;
        } else {
            throw new SQLiteException();
            //return -1;
        }
    }
    public void setBottleColor(String pillName, int bottleColor) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        cv.remove(COLUMN_BOTTLECOLOR);
        cv.put(COLUMN_BOTTLECOLOR, bottleColor);
        db.update(TABLE_NAME, cv, SELECTION, selectionArgs);
        cursor.close();
    }

    public String getPillNameFromCursor(int position) {
        Cursor cursor = readSqlDatabase();
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
    }

    public int getPrimaryKeyId(String pillName) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (cursor != null && cursor.moveToFirst()) {
            int primaryKeyId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PK));
            cursor.close();
            return primaryKeyId;
        }
        else {
            throw new SQLiteException();
        }
    }

    public Boolean checkIfPillNameExists(String pillName){
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
        String[] selectionArgs = new String[]{(pillName)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            System.out.println("Name not unique");
            return true;
        } else {
            System.out.println("Name unique");
            return false;
        }
    }

    public void createTestingPills() {
        addNewPill(1, "Testing",  new String[]{"16:10", "16:30", "16:50"},0, "2022-09-03", 30, 0, "null", 0, 2);
        //addNewPill(2, "Prozac", new String[]{"09:00"}, 1, "2022-09-03", 30, 0, "null", 0, 5);
        //addNewPill(3, "Equasym", new String[]{"09:00", "15:00"}, 0, "2022-09-03", 30, 0, "null", 0, 9);
        //addNewPill(4, "Vitamins", new String[]{"08:45", "12:00", "15:30", "19:00"}, 0, "2022-09-03", 30, 0, "null", 0, 9);
    }

    public String[] sortTimeArray(Context context, String[] timeArray) {
        DateTimeManager dateTimeManager = new DateTimeManager();
        TimeZone timeZone = TimeZone.getDefault();

        for (int arraySortAttempt = 0; arraySortAttempt < timeArray.length; arraySortAttempt++) {
            for (int currentNumber = 0; currentNumber < timeArray.length - 1; currentNumber++) {
                int nextNumber = currentNumber + 1;

                Calendar currentArrIndexCal = dateTimeManager.formatTimeStringAsCalendar(context, timeZone, timeArray[currentNumber]);
                Calendar nextArrIndexCal = dateTimeManager.formatTimeStringAsCalendar(context, timeZone, timeArray[currentNumber + 1]);

                String currentTime = dateTimeManager.formatLongAsTimeString(context, currentArrIndexCal.getTimeInMillis());
                String nextTime = dateTimeManager.formatLongAsTimeString(context, nextArrIndexCal.getTimeInMillis());

                if (currentArrIndexCal.compareTo(nextArrIndexCal) > 0) {
                    timeArray[currentNumber] = nextTime;
                    timeArray[nextNumber] = currentTime;
                }
                else if (currentArrIndexCal.compareTo(nextArrIndexCal) == 0) {
                    new Toasts().showCustomToast(context, "Error!! Cannot have the same reminder twice.");
                }
            }
        }
        return timeArray;
    }

    public String convertArrayToString(String[] array){
        String timeArrayAsString = "";
        for (int currentArrayNumber = 0; currentArrayNumber < array.length; currentArrayNumber++) {
            timeArrayAsString = timeArrayAsString + array[currentArrayNumber];

            if (currentArrayNumber < array.length - 1) {
                timeArrayAsString = timeArrayAsString+strSeparator;
            }
        }
        return timeArrayAsString;
    }
    public String[] convert24HrArrayTo12HrArray(Context context, String[] array) {
        DateTimeManager dateTimeManager = new DateTimeManager();

        for (int currentArrayNumber = 0; currentArrayNumber < array.length; currentArrayNumber++) {
            array[currentArrayNumber] = dateTimeManager.convert24HrTimeTo12HrTime(context, array[currentArrayNumber]);
        }

        return array;
    }
    public String[] convertStringToArray(String str){
        String[] timeArray = str.split(strSeparator);
        return timeArray;
    }
}




