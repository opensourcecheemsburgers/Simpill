package com.example.simpill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class Settings extends AppCompatActivity {

    Toolbar simpillToolbar;
    ImageButton settingsButton, aboutButton;
    SwitchCompat darkThemeSwitch, clockIs24HrSwitch, permanentNotificationsSwitch;
    Context myContext;
    Button deleteAllBtn;

    private Simpill simpill;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpill = new Simpill();
        this.myContext = getApplicationContext();

        loadSharedPrefs();
        setContentViewBasedOnThemeSetting();

        initWidgets();
        createOnClickListeners();

        setSupportActionBar(simpillToolbar);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                android.os.Process.killProcess(Process.myPid());
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    private void createOnClickListeners() {
        deleteAllBtn.setOnClickListener(view -> {
            DialogDatabaseReset dialogDatabaseReset = new DialogDatabaseReset();
            dialogDatabaseReset.show(getSupportFragmentManager(), "Database Reset Dialog");
        });

        aboutButton.setOnClickListener(v -> openAboutActivity());

        settingsButton.setOnClickListener(v -> showCustomToast(0));

        darkThemeSwitch.setOnClickListener(view -> {
            simpill.setCustomTheme(darkThemeSwitch.isChecked());
            SharedPreferences.Editor editor = getSharedPreferences(Simpill.THEME_PREF_BOOLEAN, MODE_PRIVATE).edit();
            editor.putBoolean(Simpill.USER_THEME, simpill.getCustomTheme());
            editor.apply();
            recreate();

            if (simpill.getCustomTheme()) {
                showCustomToast(1);
            }
            else {
                showCustomToast(2);
            }
        });

        clockIs24HrSwitch.setOnClickListener(view -> {
            simpill.setUserIs24Hr(clockIs24HrSwitch.isChecked());
            SharedPreferences.Editor editor = getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE).edit();
            editor.putBoolean(Simpill.USER_IS_24HR, simpill.getUserIs24Hr());
            editor.apply();
            recreate();

            if (simpill.getUserIs24Hr()) {
                showCustomToast(3);
            }
            else {
                showCustomToast(4);
            }
        });

        permanentNotificationsSwitch.setOnClickListener(view -> {
            simpill.setUserPermanentNotifications(permanentNotificationsSwitch.isChecked());
            SharedPreferences.Editor editor = getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_BOOLEAN, MODE_PRIVATE).edit();
            editor.putBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS, simpill.getUserPermanentNotifications());
            editor.apply();
            recreate();

            if (simpill.getUserPermanentNotifications()) {
                showCustomToast(6);
            }
            else {
                showCustomToast(7);
            }
        });
    }

    private void setContentViewBasedOnThemeSetting() {
        if (simpill.getCustomTheme())
        {
            setContentView(R.layout.settings);
        }
        else {
            setContentView(R.layout.settings_light);
        }
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getSharedPreferences(Simpill.THEME_PREF_BOOLEAN, MODE_PRIVATE);
        Boolean theme = themePref.getBoolean(Simpill.USER_THEME, true);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, false);
        simpill.setUserIs24Hr(is24Hr);
        SharedPreferences permanentNotificationsPref= myContext.getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_BOOLEAN, myContext.MODE_PRIVATE);
        Boolean permanentNotifications = permanentNotificationsPref.getBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS, false);
        simpill.setUserPermanentNotifications(permanentNotifications);
    }

    private void initWidgets() {
        simpillToolbar = findViewById(R.id.mytoolbar);
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
        darkThemeSwitch = findViewById(R.id.dark_theme_switch);
        clockIs24HrSwitch = findViewById(R.id.clock_24hr_switch);
        deleteAllBtn = findViewById(R.id.deleteAllBtn);
        permanentNotificationsSwitch = findViewById(R.id.permanentNotificationsSwitch);

        darkThemeSwitch.setChecked(simpill.getCustomTheme());
        clockIs24HrSwitch.setChecked(simpill.getUserIs24Hr());
        permanentNotificationsSwitch.setChecked(simpill.getUserPermanentNotifications());
    }

    public void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    private void showCustomToast(int toastNumber) {
        LayoutInflater layoutInflater = getLayoutInflater();

        View toastLayout;
        if (simpill.getCustomTheme()) {
            toastLayout = layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_layout));
        }
        else {
            toastLayout = layoutInflater.inflate(R.layout.custom_toast_light, findViewById(R.id.custom_toast_layout_light));
        }

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setView(toastLayout);

        TextView toastTextView = toastLayout.findViewById(R.id.custom_toast_message);
        switch(toastNumber) {
            case 0:
                toastTextView.setText("Already here :)");
                break;
            case 1:
                toastTextView.setText("Dark theme applied.");
                break;
            case 2:
                toastTextView.setText("Light theme applied.");
                break;
            case 3:
                toastTextView.setText("Time format set to 24hr.");
                break;
            case 4:
                toastTextView.setText("Time format set to 12hr.");
                break;
            case 5:
                toastTextView.setText("All pills deleted!");
                break;
            case 6:
                toastTextView.setText("Permanent notifications enabled.");
                break;
            case 7:
                toastTextView.setText("Permanent notifications disabled.");
                break;

        }
        toast.show();
    }

}
