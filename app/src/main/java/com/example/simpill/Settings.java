package com.example.simpill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class Settings extends AppCompatActivity {

    Toolbar simpillToolbar;
    ImageButton settingsButton, aboutButton;
    SwitchCompat darkThemeSwitch, clockIs24Hr;
    Context myContext;
    Button deleteAllBtn;

    private Simpill simpill;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simpill = (Simpill) getApplicationContext();

        setContentViewBasedOnThemeSetting();

        initWidgets();
        loadSharedPrefs();
        createOnClickListeners();

        this.myContext = getApplicationContext();

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

        settingsButton.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.already_in_settings), Toast.LENGTH_SHORT).show());

        darkThemeSwitch.setOnClickListener(view -> {
            simpill.setCustomTheme(darkThemeSwitch.isChecked());
            SharedPreferences.Editor editor = getSharedPreferences(Simpill.THEME_PREF_BOOLEAN, MODE_PRIVATE).edit();
            editor.putBoolean(Simpill.USER_THEME, simpill.getCustomTheme());
            editor.apply();
            recreate();
        });

        clockIs24Hr.setOnClickListener(view -> {
            simpill.setUserIs24Hr(clockIs24Hr.isChecked());
            SharedPreferences.Editor editor = getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE).edit();
            editor.putBoolean(Simpill.USER_IS_24HR, simpill.getUserIs24Hr());
            editor.apply();
            recreate();
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
    }

    private void initWidgets() {
        simpillToolbar = findViewById(R.id.mytoolbar);
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
        darkThemeSwitch = findViewById(R.id.dark_theme_switch);
        clockIs24Hr = findViewById(R.id.clock_24hr_switch);
        deleteAllBtn = findViewById(R.id.deleteAllBtn);

        darkThemeSwitch.setChecked(simpill.getCustomTheme());
        clockIs24Hr.setChecked(simpill.getUserIs24Hr());
    }

    public void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

}
