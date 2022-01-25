package com.example.simpill;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class About extends AppCompatActivity {

    private Simpill simpill;

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
        SharedPreferences themePref = getSharedPreferences(Simpill.THEME_PREF_BOOLEAN, MODE_PRIVATE);
        Boolean theme = themePref.getBoolean(Simpill.USER_THEME, true);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }

    private void setContentViewBasedOnThemeSetting() {
            setContentView(R.layout.app_about);
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

        showCustomToast(cryptoNumber);

        switch (cryptoNumber) {
            case 1:
                clipData = ClipData.newPlainText("btcAddress", btcAddress.getText().toString());
                break;
            case 2:
                clipData = ClipData.newPlainText("xmrAddress", xmrAddress.getText().toString());
                break;
            case 3:
                clipData = ClipData.newPlainText("pndAddress", pndAddress.getText().toString());
                break;
        }

        clipboardManager.setPrimaryClip(clipData);
    }

    private void showCustomToast(int toastNumber) {
        LayoutInflater layoutInflater = getLayoutInflater();

        View toastLayout = layoutInflater.inflate(R.layout.toast, findViewById(R.id.custom_toast_layout_light));

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.setView(toastLayout);

        TextView toastTextView = toastLayout.findViewById(R.id.custom_toast_message);
        switch(toastNumber) {
            case 0:
                toastTextView.setText("Already here :)");
                toast.setDuration(Toast.LENGTH_SHORT);
                break;
            case 1:
                toastTextView.setText("BTC address copied :)");
                break;
            case 2:
                toastTextView.setText("XMR address copied :)");
                break;
            case 3:
                toastTextView.setText("PND address copied :)");
                break;
        }
        toast.show();
    }

    private void setButtonOnClickListeners() {
        settingsButton.setOnClickListener(v -> openSettingsActivity());
        aboutButton.setOnClickListener(v -> showCustomToast(0));
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
