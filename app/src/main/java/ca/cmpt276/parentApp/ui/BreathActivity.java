package ca.cmpt276.parentApp.ui;

import static android.media.MediaPlayer.create;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.Breath;

//BreathActivity - guide a child to take a certain number of deep breaths in order to calm down

public class BreathActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int numBreaths;
    private int breathsCount = 0;
    private final int DEFAULT_BREATH = 1;
    private final int BREATH_DURATION = 3000;
    private final int MAX_BREATH_DURATION = 10000;
    private final int TIME_UPDATE = 30;
    private final int PROGRESS_DONE = 100;
    long startDuration = 0;
    long pressDuration = 0;
    private int inhaleProgress = 0;
    private int exhaleProgress = 0;
    private static final String NUM_BREATHS_PREF_NAME = "Number breaths";
    private static final String PREF_NAME = "AppPrefs";
    Breath breath = new Breath(DEFAULT_BREATH);
    Spinner numBreathSpinner;
    Button startBtn;
    TextView helpText, progressPercent;
    private boolean isBreathDone = false;
    private boolean isExhaling = false;
    private String buttonIn, buttonOut, buttonDone, doneMsg, inhaleMsg, exhaleMsg,startMsg;
    private ProgressBar inhaleProgressBar;
    private ProgressBar exhaleProgressBar;

    int counter = 0;
    boolean mBooleanIsPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breath);
        getSupportActionBar().setTitle(R.string.title_activity_breath);

        createSpinner();
        createStartButton();
        displayBreathsTaken();

        helpText = findViewById(R.id.help_text);
        inhaleProgressBar = findViewById(R.id.inhale_progress);
        exhaleProgressBar = findViewById(R.id.exhale_progress);
        progressPercent = findViewById(R.id.progress_percent);

        buttonIn = getString(R.string.button_in_msg);
        buttonOut = getString(R.string.button_out_msg);
        buttonDone = getString(R.string.button_done_msg);
        startMsg = getString(R.string.start_msg);
        doneMsg = getString(R.string.done_msg);
        inhaleMsg = getString(R.string.inhale_msg);
        exhaleMsg = getString(R.string.exhale_msg);

        helpText.setText(startMsg);
    }


    public void createStartButton() {
        startBtn = findViewById(R.id.start_btn);
        startBtn.setOnClickListener(v -> breathingSequence());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void breathingSequence() {

        numBreathSpinner.setEnabled(false);
        helpText.setText(inhaleMsg);
        if (numBreaths <= breathsCount) {
            startBtn.setText(buttonDone);
        }
        else {
            startBtn.setText(buttonIn);
        }
        inhaleProgressBar.setVisibility(View.VISIBLE);
        exhaleProgressBar.setVisibility(View.INVISIBLE);

        final MediaPlayer mediaPlayerInhale = create(this, R.raw.inhale);
        final MediaPlayer mediaPlayerExhale = create(this, R.raw.exhale);


        startBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (breathsCount < numBreaths) {
                        helpText.setText(inhaleMsg);
                        startBtn.setText(buttonIn);
                        inhaleProgressBar.setProgress(0);
                        inhaleProgressBar.setVisibility(View.VISIBLE);
                        exhaleProgressBar.setVisibility(View.INVISIBLE);
                        mediaPlayerInhale.start();
                        int timeInhale = TIME_UPDATE;
                        for (int progress = 0; progress < PROGRESS_DONE; progress++) {
                            createHandler(inhaleProgressBar, progress, timeInhale, progressPercent);
                            timeInhale += TIME_UPDATE;
                        }
                        inhaleHandler3000.postDelayed(inhaleRunnable3000, BREATH_DURATION);
                        inhaleHandlerLast.postDelayed(inhaleRunnableLast, MAX_BREATH_DURATION);
                        mBooleanIsPressed = true;
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (breathsCount < numBreaths) {
                        helpText.setText(exhaleMsg);
                        inhaleProgressBar.setVisibility(View.INVISIBLE);
                        exhaleProgressBar.setVisibility(View.VISIBLE);
                        mediaPlayerExhale.start();
                        int timeExhale = TIME_UPDATE;
                        for (int progress = 0; progress < PROGRESS_DONE; progress++) {
                            createHandler(exhaleProgressBar, progress, timeExhale, progressPercent);
                            timeExhale += TIME_UPDATE;
                        }
                        exhaleHandler3000.postDelayed(exhaleRunnable3000, BREATH_DURATION);
                        exhaleHandlerLast.postDelayed(exhaleRunnableLast, MAX_BREATH_DURATION);

                        if (mBooleanIsPressed) {
                            mBooleanIsPressed = false;
                            inhaleHandler3000.removeCallbacks(inhaleRunnable3000);
                            inhaleHandlerLast.removeCallbacks(inhaleRunnableLast);
                        }
                    }
                    exhaleProgressBar.setProgress(0);
                    breathsCount++;
                    helpText.setText(inhaleMsg);
                    if (numBreaths <= breathsCount) {
                        startBtn.setText(buttonDone);
                        inhaleProgressBar.setProgress(0);
                    }
                    return false;
                }
                return true;
            }

            private void createHandler(ProgressBar progressBar, int progress, int time, TextView progressPercent){
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    public void run() {
                        if (progressBar == inhaleProgressBar) {
                            if (mBooleanIsPressed) {
                                progressBar.setProgress(progress);
                                progressPercent.setText(progress+getString(R.string.percent));
                            }
                        }
                        else {
                            progressBar.setProgress(progress);
                            progressPercent.setText(progress+getString(R.string.percent));
                        }
                    }
                };

                handler.postDelayed(runnable, time);
            }

            private final Handler inhaleHandler3000 = new Handler();
            private final Runnable inhaleRunnable3000 = new Runnable() {
                public void run() {
                    if (mBooleanIsPressed) {
                        inhaleProgressBar.setProgress(PROGRESS_DONE);
                        startBtn.setText(buttonOut);
                        progressPercent.setText(PROGRESS_DONE+getString(R.string.percent));
                        mediaPlayerInhale.pause();
                    }
                    else {
                        inhaleProgressBar.setProgress(PROGRESS_DONE);
                        startBtn.setText(buttonOut);
                        progressPercent.setText(PROGRESS_DONE+getString(R.string.percent));
                        mediaPlayerInhale.pause();
                        mediaPlayerExhale.start();
                    }
                }
            };

            private final Handler inhaleHandlerLast = new Handler();
            private final Runnable inhaleRunnableLast = new Runnable() {
                public void run() {
                    if (mBooleanIsPressed) {
                        inhaleProgressBar.setVisibility(View.INVISIBLE);
                        exhaleProgressBar.setVisibility(View.VISIBLE);
                        startBtn.setText(buttonOut);
                        progressPercent.setText(PROGRESS_DONE+getString(R.string.percent));
                        mediaPlayerExhale.start();
                    }
                }
            };

            private final Handler exhaleHandler3000 = new Handler();
            private final Runnable exhaleRunnable3000 = new Runnable() {
                public void run() {
                    if (!mBooleanIsPressed) {
                        exhaleProgressBar.setProgress(PROGRESS_DONE);
                        progressPercent.setText(PROGRESS_DONE+getString(R.string.percent));
                        startBtn.setText(buttonIn);
                        mediaPlayerExhale.pause();
                    }
                    else {
                        exhaleProgressBar.setProgress(PROGRESS_DONE);
                        progressPercent.setText(PROGRESS_DONE+getString(R.string.percent));
                        startBtn.setText(buttonIn);
                        mediaPlayerExhale.pause();
                        mediaPlayerInhale.start();
                    }
                }
            };

            private final Handler exhaleHandlerLast = new Handler();
            private final Runnable exhaleRunnableLast = new Runnable() {
                public void run() {
                    if (mBooleanIsPressed) {
                        inhaleProgressBar.setVisibility(View.INVISIBLE);
                        exhaleProgressBar.setVisibility(View.VISIBLE);
                        startBtn.setText(buttonIn);
                        progressPercent.setText(PROGRESS_DONE+getString(R.string.percent));
                        mediaPlayerExhale.start();
                    }
                }
            };

        });

    }



    public void createSpinner() {
        //https://www.youtube.com/watch?v=on_OrrX7Nw4 spinner help
        numBreathSpinner = findViewById(R.id.sp_breath_option);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.preset_breath_number_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numBreathSpinner.setAdapter(adapter);
        numBreathSpinner.setOnItemSelectedListener(this);
    }


    private void saveBreaths() {
        SharedPreferences prefs = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NUM_BREATHS_PREF_NAME, numBreaths);
        editor.apply();
    }


    private int getSavedBreaths() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getInt(NUM_BREATHS_PREF_NAME, 0);
    }


    public void displayBreathsTaken() {
        int breathsTaken = getSavedBreaths();
        TextView breathText = findViewById(R.id.breaths_taken);
        String breathNum = getString(R.string.display_breaths) + breathsTaken;
        breathText.setText(breathNum);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Toast.makeText(this, "Click N to choose breaths", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                numBreaths = 1;
                break;
            case 2:
                numBreaths = 2;
                break;
            case 3:
                numBreaths = 3;
                break;
            case 4:
                numBreaths = 4;
                break;
            case 5:
                numBreaths = 5;
                break;
            case 6:
                numBreaths = 6;
                break;
            case 7:
                numBreaths = 7;
                break;
            case 8:
                numBreaths = 8;
                break;
            case 9:
                numBreaths = 9;
                break;
            case 10:
                numBreaths = 10;
                break;
        }
        breath.setNumBreath(numBreaths);
        saveBreaths();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Please choose the number of breaths", Toast.LENGTH_SHORT).show();
    }
}