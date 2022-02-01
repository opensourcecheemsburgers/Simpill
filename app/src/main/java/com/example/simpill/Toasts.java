package com.example.simpill;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Toasts {

    public void showCustomToast(Context context, String toastMessage) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View toastLayout = layoutInflater.inflate(R.layout.toast, null);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.setView(toastLayout);

        TextView toastTextView = toastLayout.findViewById(R.id.custom_toast_message);
        toastTextView.setText(toastMessage);

        toast.show();
    }
}
