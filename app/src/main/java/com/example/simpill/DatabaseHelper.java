/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.ArrayHelper.STR_SEPARATOR;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PillList.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "PillList";

    public static final String COLUMN_PK = "PrimaryKey";
    public static final String COLUMN_TITLE = "PillName";
    public static final String COLUMN_TIME = "PillTime";
    public static final String COLUMN_FREQUENCY = "PillFrequency";
    public static final String COLUMN_START_DATE = "StartDate";
    public static final String COLUMN_STOCKUP = "PillStockup";
    public static final String COLUMN_SUPPLY = "PillSupply";
    public static final String COLUMN_ISTAKEN = "IsPillTaken";
    public static final String COLUMN_TIMETAKEN = "TimeTaken";
    public static final String COLUMN_ALARMSSET = "AlarmsSet";
    public static final String COLUMN_BOTTLECOLOR = "BottleColor";
    public static final String COLUMN_CUSTOM_ALARM_URI = "CustomAlarmUri";
    public static final String COLUMN_ALARM_TYPE = "AlarmType";

    public static final int NOTIFICATION = 0;
    public static final int ALARM = 1;
    public static final int CUSTOM_ALARM = 2;

    public static final int MULTIPLE_DAILY = 0;
    public static final int DAILY = 1;
    public static final int EVERY_OTHER_DAY = 2;
    public static final int WEEKLY = 7;

    private static final String SELECTION = "PrimaryKey = ?";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "
                        + TABLE_NAME
                        + " ("
                        + COLUMN_PK
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TITLE
                        + " TEXT, "
                        + COLUMN_TIME
                        + " TEXT, "
                        + COLUMN_FREQUENCY
                        + " INTEGER, "
                        + COLUMN_START_DATE
                        + " TEXT, "
                        + COLUMN_STOCKUP
                        + " TEXT, "
                        + COLUMN_SUPPLY
                        + " INTEGER, "
                        + COLUMN_ISTAKEN
                        + " INTEGER, "
                        + COLUMN_TIMETAKEN
                        + " TEXT, "
                        + COLUMN_ALARMSSET
                        + " INTEGER, "
                        + COLUMN_ALARM_TYPE
                        + " INTEGER, "
                        + COLUMN_CUSTOM_ALARM_URI
                        + " TEXT, "
                        + COLUMN_BOTTLECOLOR
                        + " INTEGER);");
    }

    public int printDbVersion() {
        return this.getReadableDatabase().getVersion();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDbNumber, int newDbNumber) {
        String addColumn = "ALTER TABLE " + TABLE_NAME + " ADD ";

        if (oldDbNumber == 1) {
            db.execSQL(addColumn + COLUMN_FREQUENCY + " INTEGER DEFAULT 1");
            db.execSQL(addColumn + COLUMN_START_DATE + " TEXT DEFAULT 'null'");
            db.execSQL(addColumn + COLUMN_ALARMSSET + " INTEGER DEFAULT 0");
            db.execSQL(addColumn + COLUMN_ALARM_TYPE + " INTEGER DEFAULT 0");
            db.execSQL(addColumn + COLUMN_CUSTOM_ALARM_URI + "TEXT DEFAULT 'null'");
        }

        if (oldDbNumber == 2) {
            db.execSQL(addColumn + COLUMN_ALARM_TYPE + " INTEGER DEFAULT 0");
            db.execSQL(addColumn + COLUMN_CUSTOM_ALARM_URI + "TEXT DEFAULT 'null'");
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

    public Pill addPill(Pill userPill) {
        SQLiteDatabase pillDatabase = this.getWritableDatabase();
        pillDatabase.insert(TABLE_NAME, null, userPill.getContentValues());
        return getNewestPill();
    }

    public void updatePill(Pill userPill) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PK + " = ?";
        String[] selectionArgs = new String[] {String.valueOf((userPill.getPrimaryKey()))};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        cursor.moveToFirst();

        int result = db.update(TABLE_NAME, userPill.getContentValues(), SELECTION, selectionArgs);
        cursor.close();
    }

    public boolean deletePill(Pill userPill) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = new String[] {String.valueOf((userPill.getPrimaryKey()))};

        long result = db.delete(TABLE_NAME, SELECTION, selectionArgs);
        return result != -1;
    }

    public void deleteDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    private Pill getNewestPill() {
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToLast();
        Pill pill = returnPillFromCursor(cursor);
        cursor.close();
        return pill;
    }

    public Pill getPill(int primaryKey) {
        Log.d("db", "trying to find pk " + primaryKey);

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PK + " = ?";
        String[] selectionArgs = new String[] {(String.valueOf(primaryKey))};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        cursor.moveToFirst();
        Pill pill = returnPillFromCursor(cursor);
        cursor.close();
        return pill;
    }

    public Pill[] getAllPills() {
        Cursor cursor = readSqlDatabase();
        Pill[] pills = new Pill[cursor.getCount()];
        for (int index = 0; index < cursor.getCount(); index++) {
            cursor.moveToPosition(index);
            pills[index] = returnPillFromCursor(cursor);
        }
        return pills;
    }

    private Pill returnPillFromCursor(Cursor cursor) {
        return new Pill(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PK)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)).split(STR_SEPARATOR),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOCKUP)),
                Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOM_ALARM_URI))),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ISTAKEN)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMETAKEN)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUPPLY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALARM_TYPE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALARMSSET)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOTTLECOLOR))
        );
    }
}
