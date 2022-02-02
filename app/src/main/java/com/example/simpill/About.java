package com.example.simpill;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

        //findViewsByIds();
        //initiateTextViews();
        //setButtonOnClickListeners();
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getSharedPreferences(Simpill.SELECTED_THEME, MODE_PRIVATE);
        int theme = themePref.getInt(Simpill.USER_THEME, simpill.BLUE_THEME);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
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

        setContentView(R.layout.app_about_new);
    }

    private void findViewsByIds() {
        simpillParagraph = findViewById(R.id.simpill_paragraph);
        btc = findViewById(R.id.xmrTextView);
        xmr = findViewById(R.id.btcTextView);
        pnd = findViewById(R.id.pndTextView);
        btcAddress = findViewById(R.id.xmrAddressTextView);
        xmrAddress = findViewById(R.id.btcAddressTextView);
        pndAddress = findViewById(R.id.pndAddressTextView);
        btcLogo = findViewById(R.id.btcLogo);
        xmrLogo = findViewById(R.id.xmrLogo);
        pndLogo = findViewById(R.id.pndLogo);
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
    }

    private void initiateTextViews() {
        truenoLight = ResourcesCompat.getFont(this, R.font.truenolight);
        truenoReg = ResourcesCompat.getFont(this, R.font.truenoreg);

        simpillParagraph.setTypeface(truenoReg);
        simpillParagraph.setTextSize(18);
        simpillParagraph.setLineSpacing(1f, 1.35f);
        simpillParagraph.setTextIsSelectable(true);
        simpillParagraph.setLinksClickable(true);

        btc.setTypeface(truenoReg);
        btc.setTextSize(18);
        xmr.setTypeface(truenoReg);
        xmr.setTextSize(18);
        pnd.setTypeface(truenoReg);
        pnd.setTextSize(18);

        btcAddress.setTypeface(truenoLight);
        btcAddress.setTextSize(14);
        xmrAddress.setTypeface(truenoLight);
        xmrAddress.setTextSize(14);
        pndAddress.setTypeface(truenoLight);
        pndAddress.setTextSize(14);

        btc.setOnClickListener(view -> copyAddressToClipboard(1));
        xmr.setOnClickListener(view -> copyAddressToClipboard(2));
        pnd.setOnClickListener(view -> copyAddressToClipboard(3));
        btcAddress.setOnClickListener(view -> copyAddressToClipboard(1));
        xmrAddress.setOnClickListener(view -> copyAddressToClipboard(2));
        pndAddress.setOnClickListener(view -> copyAddressToClipboard(3));
        btcLogo.setOnClickListener(view -> copyAddressToClipboard(1));
        xmrLogo.setOnClickListener(view -> copyAddressToClipboard(2));
        pndLogo.setOnClickListener(view -> copyAddressToClipboard(3));
    }

    private void copyAddressToClipboard(int cryptoNumber) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService((Context.CLIPBOARD_SERVICE));
        ClipData clipData = null;

        String toastMessage = "";

        switch (cryptoNumber) {
            case 1:
                clipData = ClipData.newPlainText("btcAddress", btcAddress.getText().toString());
                toastMessage = getString(R.string.btc_address_copied);
                break;
            case 2:
                clipData = ClipData.newPlainText("xmrAddress", xmrAddress.getText().toString());
                toastMessage = getString(R.string.xmr_address_copied);
                break;
            case 3:
                clipData = ClipData.newPlainText("pndAddress", pndAddress.getText().toString());
                toastMessage = getString(R.string.pnd_address_copied);
                break;
        }
        clipboardManager.setPrimaryClip(clipData);
        toasts.showCustomToast(this, toastMessage);
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
