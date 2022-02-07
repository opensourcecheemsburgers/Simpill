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

import java.util.Calendar;

public class TimesRecyclerViewAdapter extends RecyclerView.Adapter<TimesRecyclerViewAdapter.MyViewHolder> {


    Context context;

    Simpill simpill;
    SharedPrefs sharedPrefs;
    DatabaseHelper myDatabase;
    Dialogs dialogs;
    Toasts toasts;
    DateTimeManager dateTimeManager;

    String[] times;

    int itemCount;


    TimesRecyclerViewAdapter(Context myContext, int clocks) {
        this.context = myContext;
        this.itemCount = clocks;
        this.times = new String[clocks];
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageButton clockBtn;
        Button doneBtn;
        TextView timeTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            clockBtn = itemView.findViewById(R.id.clock_image);
            timeTextView = itemView.findViewById(R.id.time_textview);
            doneBtn = itemView.findViewById(R.id.btnDone);
        }
    }

    @NonNull
    @Override
    public TimesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View clocksView = inflater.inflate(R.layout.clock_recycler_view_item, parent, false);
        MyViewHolder holder = new MyViewHolder(clocksView);
        holder.setIsRecyclable(false);

        return new TimesRecyclerViewAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.clock_recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        initClasses();
        setOnClickListeners(holder, position);

        holder.timeTextView.setText(context.getString(R.string.clocks_dialog_time_text, (position + 1)));
        if (sharedPrefs.getDarkDialogsPref(context)) {
            holder.timeTextView.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.alice_blue, null));
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
        for(int currentArrayNumber = 0; currentArrayNumber < times.length; currentArrayNumber++) {
            if (currentArrayNumber + 1 < times.length) {
                times = myDatabase.sortTimeArray(context, times);
                Calendar calendar = dateTimeManager.formatTimeStringAsCalendar(context, dateTimeManager.getUserTimezone(), times[currentArrayNumber]);
                Calendar nextCalendar = dateTimeManager.formatTimeStringAsCalendar(context, dateTimeManager.getUserTimezone(), times[currentArrayNumber + 1]);

                long currentCalTime = calendar.getTimeInMillis();
                long nextCalTime = nextCalendar.getTimeInMillis();

                if (nextCalTime - currentCalTime < 900000L) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setOnClickListeners(MyViewHolder holder, int position) {
        holder.clockBtn.setOnClickListener(view -> showTimePicker(holder, position));
        holder.timeTextView.setOnClickListener(view -> showTimePicker(holder, position));
    }


    private void showTimePicker(MyViewHolder holder, int position) {
        TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            if (!sharedPrefs.get24HourFormatPref(context)) {
                holder.timeTextView.setText(formatSelectedTimeAs12Hour(selectedHour, selectedMinute));
            } else {
                holder.timeTextView.setText(formatSelectedTimeAs24Hour(selectedHour, selectedMinute));
            }
            times[position] = formatSelectedTimeAs24Hour(selectedHour, selectedMinute);
        };
        TimePickerDialog timePickerDialog;
        if (sharedPrefs.getDarkDialogsPref(context)) {
            timePickerDialog = new TimePickerDialog(context, TimePickerDialog.THEME_HOLO_DARK, timeSetListener, 12, 0, sharedPrefs.get24HourFormatPref(context));
        } else {
            timePickerDialog = new TimePickerDialog(context, TimePickerDialog.THEME_HOLO_LIGHT, timeSetListener, 12, 0, sharedPrefs.get24HourFormatPref(context));
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
        }
        else {
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
        simpill = new Simpill();
        sharedPrefs = new SharedPrefs();
        myDatabase = new DatabaseHelper(context);
        dialogs = new Dialogs();
        toasts = new Toasts();
        dateTimeManager = new DateTimeManager();
    }

    @Override
    public int getItemCount() {
        return this.itemCount;
    }
}