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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class About extends AppCompatActivity {

    private Simpill simpill;

    ImageButton settingsButton, aboutButton;
    TextView simpillParagraph, btc, xmr, pnd, btcAddress, xmrAddress, pndAddress;
    ImageView btcLogo, xmrLogo, pndLogo;
    Typeface truenoLight;

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
        btc = findViewById(R.id.btcTextView);
        xmr = findViewById(R.id.xmrTextView);
        pnd = findViewById(R.id.pndTextView);
        btcAddress = findViewById(R.id.btcAddressTextView);
        xmrAddress = findViewById(R.id.xmrAddressTextView);
        pndAddress = findViewById(R.id.pndAddressTextView);
        btcLogo = findViewById(R.id.btcLogo);
        xmrLogo = findViewById(R.id.xmrLogo);
        pndLogo = findViewById(R.id.pndLogo);
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
    }

    private void initiateTextViews() {
        truenoLight = ResourcesCompat.getFont(this, R.font.truenolight);

        simpillParagraph.setTypeface(truenoLight);
        simpillParagraph.setTextSize(14);
        simpillParagraph.setLineSpacing(1f, 1.35f);
        simpillParagraph.setTextIsSelectable(true);
        simpillParagraph.setLinksClickable(true);

        btc.setTypeface(truenoLight);
        btc.setTextSize(14);
        xmr.setTypeface(truenoLight);
        xmr.setTextSize(14);
        pnd.setTypeface(truenoLight);
        pnd.setTextSize(14);

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

        View toastLayout;
        if (simpill.getCustomTheme()) {
            toastLayout = layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_layout));
        }
        else {
            toastLayout = layoutInflater.inflate(R.layout.custom_toast_light, findViewById(R.id.custom_toast_layout_light));
        }

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
