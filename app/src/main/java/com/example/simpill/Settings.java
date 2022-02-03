package com.example.simpill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class Settings extends AppCompatActivity implements Dialogs.SettingsDialogListener {

    Toasts toasts = new Toasts();

    Button settingsButton, aboutButton;
    SwitchCompat clockIs24HrSwitch, permanentNotificationsSwitch, darkDialogsSwitch;
    Context myContext;
    Button themesBtn, deleteAllBtn;

    private Simpill simpill;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpill = new Simpill();
        this.myContext = getApplicationContext();

        loadSharedPrefs();
        setContentViewBasedOnThemeSetting();

        initWidgets();
        createOnClickListeners();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                android.os.Process.killProcess(Process.myPid());
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    private void createOnClickListeners() {
        Dialogs getDialogs = new Dialogs();

        deleteAllBtn.setOnClickListener(view -> getDialogs.getDatabaseDeletionDialog(this).show());

        aboutButton.setOnClickListener(v -> openAboutActivity());

        settingsButton.setOnClickListener(v -> toasts.showCustomToast(getApplicationContext(), getString(R.string.already_in_about_toast)));

        themesBtn.setOnClickListener(view -> getDialogs.getChooseThemeDialog(this).show());

        darkDialogsSwitch.setOnClickListener(view -> {
            simpill.setDarkDialogs(darkDialogsSwitch.isChecked());
            SharedPreferences.Editor editor = getSharedPreferences(Simpill.DARK_DIALOGS_FILENAME, MODE_PRIVATE).edit();
            editor.putBoolean(Simpill.DARK_DIALOGS_TAG, simpill.getDarkDialogs());
            editor.apply();

            if (simpill.getDarkDialogs()) {
                toasts.showCustomToast(this, getString(R.string.dark_dialogs_toast));
            }
            else {
                toasts.showCustomToast(this, getString(R.string.light_dialogs_toast));
            }
        });

        clockIs24HrSwitch.setOnClickListener(view -> {
            simpill.setUserIs24Hr(clockIs24HrSwitch.isChecked());
            SharedPreferences.Editor editor = getSharedPreferences(Simpill.IS_24HR_BOOLEAN_FILENAME, MODE_PRIVATE).edit();
            editor.putBoolean(Simpill.USER_IS_24HR_TAG, simpill.getUserIs24Hr());
            editor.apply();

            if (simpill.getUserIs24Hr()) {
                toasts.showCustomToast(this, getString(R.string.time_format_24hr_toast));
            }
            else {
                toasts.showCustomToast(this, getString(R.string.time_format_12hr_toast));
            }
        });

        permanentNotificationsSwitch.setOnClickListener(view -> {
            simpill.setUserPermanentNotifications(permanentNotificationsSwitch.isChecked());
            SharedPreferences.Editor editor = getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_BOOLEAN, MODE_PRIVATE).edit();
            editor.putBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS_TAG, simpill.getUserPermanentNotifications());
            editor.apply();

            if (simpill.getUserPermanentNotifications()) {
                toasts.showCustomToast(this, getString(R.string.sticky_notifications_enabled_toast));
            }
            else {
                toasts.showCustomToast(this, getString(R.string.sticky_notifications_disabled_toast));
            }
        });
    }

    private void setContentViewBasedOnThemeSetting() {
        int theme = simpill.getCustomTheme();

        if (theme == simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if (theme == simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        } else if (theme == simpill.BLACK_THEME) {
            setTheme(R.style.SimpillAppTheme_BlackBackground);
        }
        else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_settings);
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getSharedPreferences(Simpill.SELECTED_THEME_FILENAME, MODE_PRIVATE);
        int theme = themePref.getInt(Simpill.USER_THEME_TAG, simpill.BLUE_THEME);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN_FILENAME, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR_TAG, false);
        simpill.setUserIs24Hr(is24Hr);
        SharedPreferences permanentNotificationsPref = myContext.getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_BOOLEAN, MODE_PRIVATE);
        Boolean permanentNotifications = permanentNotificationsPref.getBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS_TAG, false);
        simpill.setUserPermanentNotifications(permanentNotifications);
    }

    private void initWidgets() {
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
        themesBtn = findViewById(R.id.theme_select_btn);
        clockIs24HrSwitch = findViewById(R.id.clock_24hr_switch);
        deleteAllBtn = findViewById(R.id.deleteAllBtn);
        permanentNotificationsSwitch = findViewById(R.id.permanentNotificationsSwitch);

        clockIs24HrSwitch.setChecked(simpill.getUserIs24Hr());
        permanentNotificationsSwitch.setChecked(simpill.getUserPermanentNotifications());
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    @Override
    public void recreateScreen() {
        recreate();
    }
}
