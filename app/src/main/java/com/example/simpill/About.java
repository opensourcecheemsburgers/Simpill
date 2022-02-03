package com.example.simpill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class About extends AppCompatActivity {

    private Simpill simpill;

    Toasts toasts = new Toasts();

    Button settingsButton, aboutButton;
    TextView simpillParagraph, btc, xmr, pnd, btcAddress, xmrAddress, pndAddress;
    ImageView btcLogo, xmrLogo, pndLogo;
    Typeface truenoLight, truenoReg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpill = (Simpill) getApplicationContext();

        loadSharedPrefs();

        setContentViewBasedOnThemeSetting();

        findViewsByIds();
        initiateTextViews();
        setButtonOnClickListeners();
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getSharedPreferences(Simpill.SELECTED_THEME_FILENAME, MODE_PRIVATE);
        int theme = themePref.getInt(Simpill.USER_THEME_TAG, simpill.BLUE_THEME);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN_FILENAME, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR_TAG, true);
        simpill.setUserIs24Hr(is24Hr);
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

        setContentView(R.layout.app_about);
    }

    private void findViewsByIds() {
        simpillParagraph = findViewById(R.id.simpill_paragraph);
        btc = findViewById(R.id.bitcoin_title);
        xmr = findViewById(R.id.monero_title);
        pnd = findViewById(R.id.pandacoin_title);
        btcAddress = findViewById(R.id.bitcoin_address);
        xmrAddress = findViewById(R.id.monero_address);
        pndAddress = findViewById(R.id.pandacoin_address);
        btcLogo = findViewById(R.id.bitcoin_img);
        xmrLogo = findViewById(R.id.monero_img);
        pndLogo = findViewById(R.id.pandacoin_img);
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
    }

    private void initiateTextViews() {
        truenoLight = ResourcesCompat.getFont(this, R.font.truenolight);
        truenoReg = ResourcesCompat.getFont(this, R.font.truenoreg);

        ClipboardHelper clipboardHelper = new ClipboardHelper();

        btc.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 1));
        xmr.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 2));
        pnd.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 3));
        btcAddress.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 1));
        xmrAddress.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 2));
        pndAddress.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 3));
        btcLogo.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 1));
        xmrLogo.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 2));
        pndLogo.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(this, 3));
    }

    private void setButtonOnClickListeners() {
        settingsButton.setOnClickListener(v -> openSettingsActivity());
        aboutButton.setOnClickListener(v -> toasts.showCustomToast(this, getString(R.string.already_in_about_toast)));
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
