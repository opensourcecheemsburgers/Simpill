package com.example.simpill;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

public class Fonts {

    Context context;

    public Fonts(Context context) {
        this.context = context;
    }

    public Typeface getTruenoReg() {
        return ResourcesCompat.getFont(context, R.font.truenoreg);
    }

    public Typeface getTruenoLight() {
        return ResourcesCompat.getFont(context, R.font.truenolight);
    }

    public Typeface getLibertineReg() {
        return ResourcesCompat.getFont(context, R.font.libertine_reg);
    }

    public Typeface getMontserratAltReg() {
        return ResourcesCompat.getFont(context, R.font.mon_alt_reg);
    }
}
