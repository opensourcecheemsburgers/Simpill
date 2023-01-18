/* (C) 2022 */
package com.example.simpill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.content.res.AppCompatResources;

public class Toasts {

    private final Context context;

    public Toasts(Context context) {
        this.context = context;
    }

    public void showCustomToast(String toastMessage) {
        @SuppressLint("InflateParams")
        View toastLayout = LayoutInflater.from(context).inflate(R.layout.toast, null);

        if (new SharedPrefs(context).getDarkDialogsPref()) {
            toastLayout
                    .findViewById(R.id.rootToastLayout)
                    .setBackground(
                            AppCompatResources.getDrawable(context, R.drawable.toast_background));
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.setView(toastLayout);

        TextView toastTextView = toastLayout.findViewById(R.id.custom_toast_message);
        toastTextView.setText(toastMessage);

        toast.show();
    }
}
