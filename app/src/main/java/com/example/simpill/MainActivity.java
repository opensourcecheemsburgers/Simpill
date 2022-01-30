package com.example.simpill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements Dialogs.PillResetDialogListener {

    private final Simpill simpill = new Simpill();
    private final DatabaseHelper myDatabase = new DatabaseHelper(MainActivity.this);
    private final Toasts toasts = new Toasts();

    public static int backPresses = 0;

    Dialogs dialogs = new Dialogs();

    RecyclerView recyclerView;

    MainRecyclerViewAdapter myAdapter;
    Button settingsButton, aboutButton, fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSharedPrefs();

        setContentViewAndDesign();

        findViewsByIds();
        createRecyclerView();
        initiateButtons();
        isSqlDatabaseEmpty();
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getSharedPreferences(Simpill.SELECTED_THEME, MODE_PRIVATE);
        int theme = themePref.getInt(Simpill.USER_THEME, 1);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }
    private void setContentViewAndDesign() {
        int theme = simpill.getCustomTheme();

        if (theme == simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if(theme == simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        }
        else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_main);
    }

    private void findViewsByIds() {
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
        fab = findViewById(R.id.floating_action_button);
        recyclerView = findViewById(R.id.recyclerView);
    }
    private void initiateButtons() {
        settingsButton.setOnClickListener(v -> openSettingsActivity());
        aboutButton.setOnClickListener(v -> openAboutActivity());
        fab.setOnClickListener(v -> openCreatePillActivity());
    }

    private void createRecyclerView() {
        myAdapter = new MainRecyclerViewAdapter(MainActivity.this, getParent(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
    }

    public boolean onContextItemSelected(MenuItem item) {

        //This needs the pill name instead.

        String pillName = myDatabase.getPillNameFromCursor(item.getGroupId() - 1);

        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this, UpdatePill.class);
                intent.putExtra(getString(R.string.pill_name), pillName);
                intent.putExtra(getString(R.string.pill_time), myDatabase.getPillTime(pillName));
                intent.putExtra(getString(R.string.pill_date), myDatabase.getPillDate(pillName));
                intent.putExtra(getString(R.string.pill_amount), myDatabase.getPillAmount(pillName));
                intent.putExtra(getString(R.string.is_pill_taken), myDatabase.getIsTaken(pillName));
                intent.putExtra(getString(R.string.time_taken), myDatabase.getTimeTaken(pillName));
                intent.putExtra(getString(R.string.bottle_color), myDatabase.getBottleColor(pillName));
                startActivity(intent);
                backPresses = 0;
                break;
            case 2:
                dialogs.getPillDeletionDialog(this, pillName, item.getGroupId() - 1);
                break;
            case 3:
                Intent intent1 = new Intent(this, ChooseColor.class);
                intent1.putExtra(getString(R.string.pill_name), pillName);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    void isSqlDatabaseEmpty() {
        if (myDatabase.readSqlDatabase().getCount() == 0) {
            myDatabase.createTestingPills();
            new Dialogs().getWelcomeDialog(this).show();
        }
    }

    @Override
    public void onBackPressed() {
        backPresses++;

        switch (backPresses) {
            case 1:
                toasts.showCustomToast(this, "Press the back button again to exit.");
                break;
            case 2:
                closeApp();
                backPresses = 0;
                break;
        }
    }

    private void openCreatePillActivity() {
        Intent intent = new Intent(this, CreatePill.class);
        startActivity(intent);
    }
    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        backPresses = 0;
    }
    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
        backPresses = 0;
    }

    public void closeApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        System.exit(0);
    }

    @Override
    public void notifyAdapterOfDeletedPill(int position) {
        notifyAdapterOfDeletedPill(position);
    }
}