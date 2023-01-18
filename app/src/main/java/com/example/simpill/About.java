/* (C) 2022 */
package com.example.simpill;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class About extends AppCompatActivity {

    final Toasts toasts = new Toasts(this);

    Button aboutButton, paypalButton, xmrButton, btcButton;
    TextView simpillParagraph;
    Typeface truenoLight, truenoReg;
    private final int XMR = 1, BTC = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewBasedOnThemeSetting();
        findViewsByIds();
        initiateTextViews();
        setButtonOnClickListeners();
    }

    private void setContentViewBasedOnThemeSetting() {
        int theme = new SharedPrefs(this).getThemesPref();

        if (theme == Simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if (theme == Simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        } else if (theme == Simpill.BLACK_THEME) {
            setTheme(R.style.SimpillAppTheme_BlackBackground);
        } else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_about);
    }

    private void findViewsByIds() {
        simpillParagraph = findViewById(R.id.simpill_paragraph);
        aboutButton = findViewById(R.id.back_button);
        paypalButton = findViewById(R.id.paypal_btn);
        xmrButton = findViewById(R.id.xmr_btn);
        btcButton = findViewById(R.id.btc_btn);
    }

    private void initiateTextViews() {
        truenoLight = ResourcesCompat.getFont(this, R.font.inter_reg);
        truenoReg = ResourcesCompat.getFont(this, R.font.inter_medium);

        paypalButton.setOnClickListener(
                view ->
                        startActivity(
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(this.getString(R.string.paypal_donation_link)))));
        btcButton.setOnClickListener(view -> copyCryptoAddressToClipboard(BTC));
        xmrButton.setOnClickListener(view -> copyCryptoAddressToClipboard(XMR));
    }

    private void copyCryptoAddressToClipboard(int cryptoNumber) {
        ClipboardManager clipboardManager =
                (ClipboardManager) this.getSystemService((Context.CLIPBOARD_SERVICE));
        ClipData clipData = null;

        String toastMessage = "";

        switch (cryptoNumber) {
            case BTC:
                clipData =
                        ClipData.newPlainText(
                                "btcAddress", this.getString(R.string.bitcoin_address));
                toastMessage = this.getString(R.string.btc_address_copied);
                break;
            case XMR:
                clipData =
                        ClipData.newPlainText(
                                "xmrAddress", this.getString(R.string.monero_address));
                toastMessage = this.getString(R.string.xmr_address_copied);
                break;
        }
        clipboardManager.setPrimaryClip(clipData);
        toasts.showCustomToast(toastMessage);
    }

    private void setButtonOnClickListeners() {
        aboutButton.setOnClickListener(v -> finish());
    }
}
