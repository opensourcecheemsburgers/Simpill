/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.Simpill.BLUE_THEME;

import android.content.Context;
import android.content.res.Configuration;

public class SharedPrefs {

    public static final String OPEN_COUNT_FILENAME = "open_count";
    public static final String DARK_DIALOGS_FILENAME = "dark_dialogs";
    public static final String SELECTED_THEME_FILENAME = "User Theme";
    public static final String IS_24HR_BOOLEAN_FILENAME = "Is 24Hr Boolean";
    public static final String PERMANENT_NOTIFICATIONS_FILENAME = "Permanent Notification Boolean";
    public static final String SOUND_ON_FILENAME = "sound";

    public static final String DARK_DIALOGS_TAG = "Dark Dialogs";
    public static final String OPEN_COUNT_TAG = "Open Count";
    public static final String USER_THEME_TAG = "User Theme";
    public static final String USER_IS_24HR_TAG = "User Is24Hr";
    public static final String USER_PERMANENT_NOTIFICATIONS_TAG = "User PermanentNotifications";
    public static final String SOUND_ON_TAG = "Sound On";

    private final Context context;

    public SharedPrefs(Context context) {
        this.context = context;
    }

    public void setDarkDialogsPref(boolean darkDialogs) {
        context.getSharedPreferences(DARK_DIALOGS_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(DARK_DIALOGS_TAG, darkDialogs)
                .apply();
    }

    public boolean getDarkDialogsPref() {
        return context.getSharedPreferences(DARK_DIALOGS_FILENAME, Context.MODE_PRIVATE)
                .getBoolean(
                        DARK_DIALOGS_TAG,
                        (context.getResources().getConfiguration().uiMode
                                        & Configuration.UI_MODE_NIGHT_MASK)
                                == Configuration.UI_MODE_NIGHT_YES);
    }

    public void setThemesPref(int theme) {
        context.getSharedPreferences(SELECTED_THEME_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(USER_THEME_TAG, theme)
                .apply();
    }

    public int getThemesPref() {
        return context.getSharedPreferences(SELECTED_THEME_FILENAME, Context.MODE_PRIVATE)
                .getInt(USER_THEME_TAG, BLUE_THEME);
    }

    public void setOpenCountPref(int openCount) {
        context.getSharedPreferences(OPEN_COUNT_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(OPEN_COUNT_TAG, openCount)
                .apply();
    }

    public int getOpenCountPref() {
        return context.getSharedPreferences(OPEN_COUNT_FILENAME, Context.MODE_PRIVATE)
                .getInt(OPEN_COUNT_TAG, 0);
    }

    public void setStickyNotificationsPref(boolean stickyNotifications) {
        context.getSharedPreferences(PERMANENT_NOTIFICATIONS_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(USER_PERMANENT_NOTIFICATIONS_TAG, stickyNotifications)
                .apply();
    }

    public boolean getStickyNotificationsPref() {
        return context.getSharedPreferences(PERMANENT_NOTIFICATIONS_FILENAME, Context.MODE_PRIVATE)
                .getBoolean(USER_PERMANENT_NOTIFICATIONS_TAG, false);
    }

    public void set24HourTimeFormatPref(boolean is24HourFormat) {
        context.getSharedPreferences(IS_24HR_BOOLEAN_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(USER_IS_24HR_TAG, is24HourFormat)
                .apply();
    }

    public boolean get24HourFormatPref() {
        return context.getSharedPreferences(IS_24HR_BOOLEAN_FILENAME, Context.MODE_PRIVATE)
                .getBoolean(USER_IS_24HR_TAG, true);
    }

    public void setPillSoundPref(boolean soundOn) {
        context.getSharedPreferences(SOUND_ON_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SOUND_ON_TAG, soundOn)
                .apply();
    }

    public boolean getPillSoundPref() {
        return context.getSharedPreferences(SOUND_ON_FILENAME, Context.MODE_PRIVATE)
                .getBoolean(SOUND_ON_TAG, true);
    }
}
