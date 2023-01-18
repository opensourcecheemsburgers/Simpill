/* (C) 2022 */
package com.example.simpill;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TimesRecyclerViewAdapter
        extends RecyclerView.Adapter<TimesRecyclerViewAdapter.MyViewHolder> {

    final Context context;

    SharedPrefs sharedPrefs;
    DatabaseHelper myDatabase;
    Dialogs dialogs;
    Toasts toasts;
    DateTimeManager dateTimeManager;

    String[] times;

    final int itemCount;

    TimesRecyclerViewAdapter(Context myContext, int clocks) {
        this.context = myContext;
        this.itemCount = clocks;
        this.times = new String[clocks];
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageButton clockBtn;
        final Button doneBtn;
        final TextView timeTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            clockBtn = itemView.findViewById(R.id.clock_image);
            timeTextView = itemView.findViewById(R.id.time_textview);
            doneBtn = itemView.findViewById(R.id.btnDone);
        }
    }

    @NonNull @Override
    public TimesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View clocksView = inflater.inflate(R.layout.clock_recycler_view_item, parent, false);
        MyViewHolder holder = new MyViewHolder(clocksView);
        holder.setIsRecyclable(false);

        return new TimesRecyclerViewAdapter.MyViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.clock_recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        initClasses();
        setOnClickListeners(holder, position);

        holder.timeTextView.setText(
                context.getString(R.string.clocks_dialog_time_text, (position + 1)));
        if (sharedPrefs.getDarkDialogsPref()) {
            holder.timeTextView.setTextColor(
                    ResourcesCompat.getColor(context.getResources(), R.color.alice_blue, null));
        }
    }

    public String[] returnTimeStringsArrayFromRecyclerViewClass() {
        return times;
    }

    public boolean checkForEmptyTimes() {
        for (String time : times) {
            if (time == null) {
                return true;
            }
        }
        return false;
    }

    public boolean checkForAdjacentTimes() {
        for (int currentArrayNumber = 0; currentArrayNumber < times.length; currentArrayNumber++) {
            if (currentArrayNumber + 1 < times.length) {
                times = new ArrayHelper().sortTimeArray(times);
                long currentReminderTime =
                        dateTimeManager.convertTimeToCurrentDateTimeInMillis(
                                times[currentArrayNumber]);
                long nextReminderTime =
                        dateTimeManager.convertTimeToCurrentDateTimeInMillis(
                                times[currentArrayNumber + 1]);

                System.out.println(
                        "Checking times " + currentReminderTime + " and " + nextReminderTime);
                System.out.println("Checking times " + (nextReminderTime - currentReminderTime));

                long difference = nextReminderTime - currentReminderTime;
                return difference > 0 ? difference < 900000L : difference * -1 < 900000L;
            }
        }
        return false;
    }

    private void setOnClickListeners(MyViewHolder holder, int position) {
        holder.clockBtn.setOnClickListener(view -> showTimePicker(holder, position));
        holder.timeTextView.setOnClickListener(view -> showTimePicker(holder, position));
    }

    private void showTimePicker(MyViewHolder holder, int position) {
        TimePickerDialog.OnTimeSetListener timeSetListener =
                (timePicker, selectedHour, selectedMinute) -> {
                    if (!sharedPrefs.get24HourFormatPref()) {
                        holder.timeTextView.setText(
                                formatSelectedTimeAs12Hour(selectedHour, selectedMinute));
                    } else {
                        holder.timeTextView.setText(
                                formatSelectedTimeAs24Hour(selectedHour, selectedMinute));
                    }
                    times[position] = formatSelectedTimeAs24Hour(selectedHour, selectedMinute);
                };
        TimePickerDialog timePickerDialog;
        if (sharedPrefs.getDarkDialogsPref()) {
            timePickerDialog =
                    new TimePickerDialog(
                            context,
                            TimePickerDialog.THEME_HOLO_DARK,
                            timeSetListener,
                            12,
                            0,
                            sharedPrefs.get24HourFormatPref());
        } else {
            timePickerDialog =
                    new TimePickerDialog(
                            context,
                            TimePickerDialog.THEME_HOLO_LIGHT,
                            timeSetListener,
                            12,
                            0,
                            sharedPrefs.get24HourFormatPref());
        }
        timePickerDialog.show();
    }

    private String formatSelectedTimeAs12Hour(int selectedHour, int selectedMinute) {
        String amOrPm, timeIn12HourFormat;

        if (selectedHour > 12) {
            amOrPm = "pm";
            selectedHour = selectedHour - 12;
        } else if (selectedHour == 12) {
            amOrPm = "pm";
        } else if (selectedHour == 0) {
            selectedHour = selectedHour + 12;
            amOrPm = "am";
        } else {
            amOrPm = "am";
        }
        if (selectedMinute < 10) {
            timeIn12HourFormat = selectedHour + ":0" + selectedMinute + " " + amOrPm;
        } else {
            timeIn12HourFormat = selectedHour + ":" + selectedMinute + " " + amOrPm;
        }
        return timeIn12HourFormat;
    }

    private String formatSelectedTimeAs24Hour(int selectedHour, int selectedMinute) {
        String timeIn24HourFormat;

        if (selectedMinute < 10) {
            timeIn24HourFormat = selectedHour + ":0" + selectedMinute;
        } else {
            timeIn24HourFormat = selectedHour + ":" + selectedMinute;
        }
        if (selectedHour < 10) {
            timeIn24HourFormat = "0" + selectedHour + ":" + selectedMinute;
        }
        if (selectedHour < 10 && selectedMinute < 10) {
            timeIn24HourFormat = "0" + selectedHour + ":0" + selectedMinute;
        }
        return timeIn24HourFormat;
    }

    private void initClasses() {
        sharedPrefs = new SharedPrefs(context);
        myDatabase = new DatabaseHelper(context);
        dialogs = new Dialogs(context);
        toasts = new Toasts(context);
        dateTimeManager = new DateTimeManager();
    }

    @Override
    public int getItemCount() {
        return this.itemCount;
    }
}
