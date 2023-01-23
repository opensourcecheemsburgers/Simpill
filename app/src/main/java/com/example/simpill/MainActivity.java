/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.CreatePill.NEW_PILL_INTENT_KEY;
import static com.example.simpill.Pill.PILL_TAKEN_VIA_NOTIFICATION_INTENT_KEY;
import static com.example.simpill.Pill.PRIMARY_KEY_INTENT_KEY_STRING;
import static com.example.simpill.Simpill.CRASH_DATA_INTENT_KEY_STRING;
import static com.example.simpill.Simpill.IS_CRASH_INTENT_KEY_STRING;

import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements Pill.PillListener {

    private final SharedPrefs sharedPrefs = new SharedPrefs(this);
    private final DatabaseHelper myDatabase = new DatabaseHelper(this);
    private final ArrayHelper arrayHelper = new ArrayHelper();
    private final Toasts toasts = new Toasts(this);
    public Pill[] pills;

    public static int backPresses = 0;

    final Dialogs dialogs = new Dialogs(this);

    RecyclerView recyclerView;

    MainRecyclerViewAdapter myAdapter;
    Button settingsButton, aboutButton, fab;

    AudioHelper audioHelper;
    MediaPlayer takenPlayer, resetPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(IS_CRASH_INTENT_KEY_STRING)
                && intent.getBooleanExtra(IS_CRASH_INTENT_KEY_STRING, true)
                && intent.hasExtra(CRASH_DATA_INTENT_KEY_STRING)) {
            dialogs.getCrashDialog(intent.getStringExtra(CRASH_DATA_INTENT_KEY_STRING)).show();
        }
        loadPillsFromDatabase();
        checkOpenCount();
        setContentViewAndDesign();
        findViewsByIds();
        createRecyclerView();
        makeRecyclerViewItemsSwipeable();
        initiateButtons();
        isSqlDatabaseEmpty();
        initSoundEffects();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean newPillAdded = intent.hasExtra(NEW_PILL_INTENT_KEY);
        int pk = intent.getIntExtra(NEW_PILL_INTENT_KEY, -1);

        if(newPillAdded && pk != -1) {
            Pill[] newPillArray = arrayHelper.addPillToPillArray(pills, myDatabase.getPill(intent.getIntExtra(NEW_PILL_INTENT_KEY, -1)));
            pills = newPillArray;
            myAdapter.pills = newPillArray;
            myAdapter.notifyItemInserted(newPillArray.length - 1);
        }
        onNotificationClicked(intent);
    }

    private void onNotificationClicked(Intent intent) {
        if (intent.hasExtra(PILL_TAKEN_VIA_NOTIFICATION_INTENT_KEY)) {
            int pk = intent.getIntExtra(PILL_TAKEN_VIA_NOTIFICATION_INTENT_KEY, -1);
            ArrayHelper arrayHelper = new ArrayHelper();
            int position = arrayHelper.findPillUsingPrimaryKey(pills, pk);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            System.out.println("Getting position = " + position);
            if (layoutManager != null) {
                View pillView = layoutManager.findViewByPosition(position);
                if (pillView != null) {
                    pillView.startAnimation(
                            AnimationUtils.loadAnimation(this, R.anim.recycler_item_enlarge));
                }
            }
        }
    }

    private void initSoundEffects() {
        audioHelper = new AudioHelper(this);
        takenPlayer = audioHelper.getTakenPlayer();
        resetPlayer = audioHelper.getResetPlayer();
    }

    private void makeRecyclerViewItemsSwipeable() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getLayoutPosition();

                        Pill pill = pills[viewHolder.getAbsoluteAdapterPosition()];

                        switch (direction) {
                            case ItemTouchHelper.RIGHT:
                                myAdapter.notifyItemChanged(position);
                                dialogs.getPillDeletionDialog(pill, position).show();
                                break;
                            case ItemTouchHelper.LEFT:
                                myAdapter.notifyItemChanged(position);
                                openUpdatePill(pill);
                                break;
                        }
                    }

                    @Override
                    public void onChildDraw(
                            @NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {

                        new RecyclerViewSwipeDecorator.Builder(
                                        c,
                                        recyclerView,
                                        viewHolder,
                                        dX * 0.85f,
                                        dY * 0.85f,
                                        actionState,
                                        isCurrentlyActive)
                                .addSwipeRightActionIcon(R.drawable.ic_delete_svgrepo_com)
                                .addSwipeLeftActionIcon(R.drawable.ic_write_svgrepo_com)
                                .create()
                                .decorate();

                        super.onChildDraw(
                                c,
                                recyclerView,
                                viewHolder,
                                dX * 0.85f,
                                dY * 0.85f,
                                actionState,
                                isCurrentlyActive);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void checkOpenCount() {
        int count = sharedPrefs.getOpenCountPref();
        if (count == 0) {
            dialogs.getWelcomeDialog().show();
        } else {
            if (count % 150 == 0) dialogs.getDonationDialog().show();
        }
        count = count + 1;
        sharedPrefs.setOpenCountPref(count);
    }

    private void setContentViewAndDesign() {
        int theme = sharedPrefs.getThemesPref();
        if (theme == Simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if (theme == Simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        } else if (theme == Simpill.BLACK_THEME) {
            setTheme(R.style.SimpillAppTheme_BlackBackground);
        } else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }
        setContentView(R.layout.app_main);
    }

    private void findViewsByIds() {
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.about_button);
        fab = findViewById(R.id.floating_action_button);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initiateButtons() {
        settingsButton.setOnClickListener(v -> openSettingsActivity());
        aboutButton.setOnClickListener(v -> openAboutActivity());
        fab.setOnClickListener(v -> openCreatePillActivity());
    }

    private void loadPillsFromDatabase() {
        pills = myDatabase.getAllPills();
        for (Pill pill : pills) {
            if (pill.getAlarmsSet() == 0) {
                pill.setAlarm(this);
                pill.setStockupAlarm(this);
                pill.setAlarmsSet(1);
            }
        }
    }

    private void createRecyclerView() {
        myAdapter = new MainRecyclerViewAdapter(MainActivity.this, getParent(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
    }

    public boolean onContextItemSelected(MenuItem item) {
        Pill pill = pills[item.getGroupId() - 1];

        switch (item.getItemId()) {
            case 1:
                openUpdatePill(pill);
                break;
            case 2:
                dialogs.getPillDeletionDialog(pill, item.getGroupId() - 1).show();
                break;
            case 3:
                Intent changeColorIntent = new Intent(this, ChooseColor.class);
                changeColorIntent.putExtra(PRIMARY_KEY_INTENT_KEY_STRING, pill.getPrimaryKey());
                startActivity(changeColorIntent);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    void isSqlDatabaseEmpty() {
        if (myDatabase.readSqlDatabase().getCount() == 0) {
            LottieAnimationView lottieAnimationView = findViewById(R.id.animationView);
            lottieAnimationView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            TextView emptyDbTextView = findViewById(R.id.no_pills_textview);
            Button button = findViewById(R.id.add_btn_main);
            emptyDbTextView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> openCreatePillActivity());
            fab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        backPresses++;

        switch (backPresses) {
            case 1:
                toasts.showCustomToast(this.getString(R.string.press_back_again_toast));
                break;
            case 2:
                closeApp();
                backPresses = 0;
                break;
        }
    }

    private void openUpdatePill(Pill pill) {
        Intent intent = new Intent(this, CreatePill.class);
        intent.putExtra(PRIMARY_KEY_INTENT_KEY_STRING, pill.getPrimaryKey());
        startActivity(intent);
        backPresses = 0;
    }

    private void openCreatePillActivity() {
        Intent intent = new Intent(this, CreatePill.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
        backPresses = 0;
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
        backPresses = 0;
    }

    public void closeApp() {
        ActivityCompat.finishAffinity(this);
        System.exit(0);
    }

    @Override
    public void notifyAddedPill(Pill pill) {
        Pill[] newPillArray = arrayHelper.addPillToPillArray(myAdapter.pills, pill);
        this.pills = newPillArray;
        myAdapter.pills = newPillArray;
        myAdapter.notifyItemInserted(newPillArray.length - 1);
    }

    @Override
    public void notifyDeletedPill(Pill pill, int position) {
        Pill[] newPillArray = arrayHelper.deletePillFromPillArray(myAdapter.pills, pill);
        this.pills = newPillArray;
        myAdapter.pills = newPillArray;
        myAdapter.notifyItemRemoved(position);
        myAdapter.notifyItemRangeChanged(position, newPillArray.length);
    }

    @Override
    public void notifyResetPill(int position) {
        myAdapter.notifyItemChanged(position);
    }
}
