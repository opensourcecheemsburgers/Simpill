package com.example.simpill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class ChooseColor extends AppCompatActivity {

    private Simpill simpill;

    PillDBHelper myDatabase = new PillDBHelper(this);
    int selectedColor = -1;
    ImageView color1, color2, color3, color4, color5, color6,
            color7, color8, color9, color10, color11, color12;
    ImageButton settingsButton, aboutButton;
    Typeface truenoReg;
    TextView chooseColorTextview;

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

        chooseColorTextview = findViewById(R.id.choose_color_textview);
        truenoReg = ResourcesCompat.getFont(this, R.font.truenoreg);
        chooseColorTextview.setTypeface(truenoReg);

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

    private void createOnClickListeners(String pillName){
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
        SharedPreferences themePref = getSharedPreferences(Simpill.THEME_PREF_BOOLEAN, MODE_PRIVATE);
        Boolean theme = themePref.getBoolean(Simpill.USER_THEME, true);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }

    private void setContentViewBasedOnThemeSetting() {
        if (simpill.getCustomTheme())
        {
            setContentView(R.layout.choose_color);
        }
        else {
            setContentView(R.layout.choose_color_light);
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

}
