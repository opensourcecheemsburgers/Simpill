package com.example.simpill;

import android.content.Intent;
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
    Button themesBtn, deleteAllBtn;

    private final SharedPrefs sharedPrefs = new SharedPrefs();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

            sharedPrefs.setDarkDialogsPref(this, darkDialogsSwitch.isChecked());

            if (sharedPrefs.getDarkDialogsPref(this)) {
                toasts.showCustomToast(this, getString(R.string.dark_dialogs_toast));
            }
            else {
                toasts.showCustomToast(this, getString(R.string.light_dialogs_toast));
            }
        });

        clockIs24HrSwitch.setOnClickListener(view -> {
            sharedPrefs.set24HourTimeFormatPref(this, clockIs24HrSwitch.isChecked());

            if (sharedPrefs.get24HourFormatPref(this)) {
                toasts.showCustomToast(this, getString(R.string.time_format_24hr_toast));
            }
            else {
                toasts.showCustomToast(this, getString(R.string.time_format_12hr_toast));
            }
        });

        permanentNotificationsSwitch.setOnClickListener(view -> {
            sharedPrefs.setStickyNotificationsPref(this, permanentNotificationsSwitch.isChecked());

            if (permanentNotificationsSwitch.isChecked()) {
                toasts.showCustomToast(this, getString(R.string.sticky_notifications_enabled_toast));
            }
            else {
                toasts.showCustomToast(this, getString(R.string.sticky_notifications_disabled_toast));
            }
        });
    }

    private void setContentViewBasedOnThemeSetting() {
        int theme = sharedPrefs.getThemesPref(this);

        if (theme == Simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if (theme == Simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        } else if (theme == Simpill.BLACK_THEME) {
            setTheme(R.style.SimpillAppTheme_BlackBackground);
        }
        else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_settings);
    }

    private void initWidgets() {
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
        themesBtn = findViewById(R.id.theme_select_btn);
        clockIs24HrSwitch = findViewById(R.id.clock_24hr_switch);
        darkDialogsSwitch = findViewById(R.id.dark_dialogs_switch);
        deleteAllBtn = findViewById(R.id.deleteAllBtn);
        permanentNotificationsSwitch = findViewById(R.id.permanentNotificationsSwitch);

        clockIs24HrSwitch.setChecked(sharedPrefs.get24HourFormatPref(this));
        darkDialogsSwitch.setChecked(sharedPrefs.getDarkDialogsPref(this));
        permanentNotificationsSwitch.setChecked(sharedPrefs.getStickyNotificationsPref(this));
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
