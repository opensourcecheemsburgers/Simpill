package com.example.simpill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class ChooseColor extends AppCompatActivity {

    private Simpill simpill;

    DatabaseHelper myDatabase = new DatabaseHelper(this);
    int selectedColor = -1;
    ImageView color1, color2, color3, color4, color5, color6,
            color7, color8, color9, color10, color11, color12;
    Button settingsButton, aboutButton;
    Typeface truenoReg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpill = (Simpill) getApplicationContext();
        setContentViewBasedOnThemeSetting();

        String pillName = getIntent().getStringExtra(getString(R.string.pill_name));

        loadSharedPrefs();
        findViewsByIds();
        createOnClickListeners(pillName);
    }

    private void findViewsByIds() {
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);

        truenoReg = ResourcesCompat.getFont(this, R.font.truenoreg);

        color1 = findViewById(R.id.imageView1);
        color2 = findViewById(R.id.imageView2);
        color3 = findViewById(R.id.imageView3);
        color4 = findViewById(R.id.imageView4);
        color5 = findViewById(R.id.imageView5);
        color6 = findViewById(R.id.imageView6);
        color7 = findViewById(R.id.imageView7);
        color8 = findViewById(R.id.imageView8);
        color9 = findViewById(R.id.imageView9);
        color10 = findViewById(R.id.imageView10);
        color11 = findViewById(R.id.imageView11);
        color12 = findViewById(R.id.imageView12);
    }

    private void createOnClickListeners(String pillName) {
        settingsButton.setOnClickListener(view -> openSettingsActivity());
        aboutButton.setOnClickListener(view -> openAboutActivity());

        color1.setOnClickListener(view -> {
            selectedColor = 1;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color2.setOnClickListener(view -> {
            selectedColor = 2;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color3.setOnClickListener(view -> {
            selectedColor = 3;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color4.setOnClickListener(view -> {
            selectedColor = 4;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color5.setOnClickListener(view -> {
            selectedColor = 5;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color6.setOnClickListener(view -> {
            selectedColor = 6;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color7.setOnClickListener(view -> {
            selectedColor = 7;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color8.setOnClickListener(view -> {
            selectedColor = 8;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color9.setOnClickListener(view -> {
            selectedColor = 9;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color10.setOnClickListener(view -> {
            selectedColor = 10;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color11.setOnClickListener(view -> {
            selectedColor = 11;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
        color12.setOnClickListener(view -> {
            selectedColor = 12;
            myDatabase.setBottleColor(pillName, selectedColor);
            openMainActivity();
        });
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getSharedPreferences(Simpill.SELECTED_THEME, MODE_PRIVATE);
        int theme = themePref.getInt(Simpill.USER_THEME, simpill.BLUE_THEME);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref = getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setContentViewBasedOnThemeSetting() {
        int theme = simpill.getCustomTheme();

        if (theme == simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if(theme == simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        }
        else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_choose_color);
    }
}