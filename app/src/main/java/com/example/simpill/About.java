package com.example.simpill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class About extends AppCompatActivity {

    private Simpill simpill;

    ImageButton settingsButton, aboutButton;
    TextView simpillParagraph;
    Typeface truenoReg, libertineReg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpill = (Simpill) getApplicationContext();

        loadSharedPrefs();

        setContentViewBasedOnThemeSetting();

        findViewsByIds();
        initiateTextView();
        initiateButtons();
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
            setContentView(R.layout.about);
        }
        else {
            setContentView(R.layout.about_light);
        }
    }

    private void findViewsByIds() {
        simpillParagraph = findViewById(R.id.simpill_paragraph);
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
    }

    private void initiateTextView() {
        truenoReg = ResourcesCompat.getFont(this, R.font.truenoreg);
        libertineReg = ResourcesCompat.getFont(this, R.font.libertine_reg);
        simpillParagraph.setTypeface(libertineReg);
        simpillParagraph.setTextSize(20);
        simpillParagraph.setLineSpacing(1f, 1.35f);
        simpillParagraph.setTextIsSelectable(true);
        simpillParagraph.setLinksClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            simpillParagraph.setLetterSpacing(0.01f);
        }
    }
    private void initiateButtons() {
        settingsButton.setOnClickListener(v -> openSettingsActivity());
        aboutButton.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.already_about),
                        Toast.LENGTH_LONG).show());
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
