package ca.cmpt276.parentApp.ui;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Locale;

import ca.cmpt276.parentApp.R;

/**
 * TimeoutActivity class - display a timer and minute options for user to choose, pause, and reset
 */
public class TimeoutActivity extends AppCompatActivity {

    //https://www.youtube.com/watch?v=lvibl8YJfGo, https://www.youtube.com/watch?v=7dQJAkjNEjM timer help


    private EditText inputText;
    private TextView countDownText;
    private TextView selectTimerLength;
    private TextView syntaxOfUserInput;
    private TextView customizeTimerLength;

    private Button setBtn;
    private Button startPauseBtn;
    private Button resetBtn;

    private CountDownTimer countDownTime;

    private boolean isTimerRunning;

    private long startInMilliseconds;
    private long timeLeftInMilliseconds;
    private long finishTime;

    private final long MILLISECOND_TO_MINUTE = 60000;
    private final long MILLISECOND_TO_SECOND = 1000;

    RadioGroup group;
    Vibrator vibrator;
    MediaPlayer alarmSound;
    private TextView speedStatement;
    private int rate = 100;
    private boolean isTimerPause = false;

    private final String PREFS = "TimePrefs";
    private final String START_TIME = "StartTimeInMilliseconds";
    private final String TIMER_RUNNING = "TimerRunning";
    private final String TIME_LEFT = "MillisecondsLeft";
    private final String TIME_FINISHED = "EndTime";
    private final String ID_NAME = "Timer Done";
    private final String CHANNEL_ID = "Timer";
    private final String CHANNEL_DESC = "Channel for timer notifications";
    private final String EXTRA_NAME = "Snooze";
    private final String EXTRA_TITLE = "Timer is done";
    private final String ACTION_NAME = "Stop";

    private ProgressBar progressPie;
    private final int noProgressYet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        getSupportActionBar().setTitle(R.string.title_activity_timeout);

        inputText = findViewById(R.id.inputTextTimerLength);
        countDownText = findViewById(R.id.countDownText);
        selectTimerLength = findViewById(R.id.textViewSelectTimerLength);
        syntaxOfUserInput= findViewById(R.id.textViewUserInputSyntax);
        customizeTimerLength = findViewById(R.id.textViewCustomizeTimerLength);


        setBtn = findViewById(R.id.setBtn);
        startPauseBtn = findViewById(R.id.startPauseBtn);
        resetBtn = findViewById(R.id.resetBtn);

        group = findViewById(R.id.radioGroupTimerLength);

        setBtn.setOnClickListener(v -> {

            String userInput = inputText.getText().toString();

            if (userInput.length() == 0) {

                Toast.makeText(TimeoutActivity.this, getString(R.string.enter_minutes), Toast.LENGTH_SHORT).show();
                return;

            }

            long inputInMilliseconds = Long.parseLong(userInput) * MILLISECOND_TO_MINUTE;

            if (inputInMilliseconds == 0) {

                Toast.makeText(TimeoutActivity.this, getString(R.string.enter_positive_number), Toast.LENGTH_SHORT).show();
                return;

            }

            setTime(inputInMilliseconds);
            inputText.setText("");
        });

        startPauseBtn.setOnClickListener(v -> {
            if (isTimerRunning) {

                pauseTimer();

            } else {

                startTimer();

            }
        });

        resetBtn.setOnClickListener(v -> {

            if (alarmSound != null) {
                alarmSound.release();
            }

            if (countDownTime != null) {
                if (isTimerRunning = true) {
                    pauseTimer();
                }
            }

            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            resetTimer();

        });
        createNotificationChannel();
        registerReceiver(soundReceiver, new IntentFilter(ACTION_NAME));
        createRadioButtons();

        timeLeftInMilliseconds = MILLISECOND_TO_MINUTE;
        refreshCountDown();

    }//onCreate ends

    private void createNotificationChannel() {
        //https://www.youtube.com/watch?v=4BuRMScaaI4 notification help
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, ID_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void createNotification() {

        //https://stackoverflow.com/questions/47409256/what-is-notification-channel-idnotifications-not-work-in-api-27
        //global notification help
        Intent soundIntent = new Intent(getApplicationContext(), NotificationSnooze.class);
        soundIntent.putExtra(EXTRA_NAME, EXTRA_TITLE);
        PendingIntent pendingSoundIntent = PendingIntent.getBroadcast(this,
                0, soundIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(getString(R.string.timer_done))
                .setContentText(getString(R.string.notification_message))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_kids)
                .addAction(R.drawable.ic_hourglass,EXTRA_NAME,pendingSoundIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                //https://www.youtube.com/watch?v=j6kQ9gikU-A
                .setContentIntent(pendingSoundIntent);



        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(TimeoutActivity.this);
        managerCompat.notify(1, builder.build());

    }


    private void playSound() {

        //https://www.youtube.com/watch?v=9oj4f8721LM sound reference
        alarmSound = MediaPlayer.create(this, R.raw.alarm_sound);
        //sound from free sounds.org
        alarmSound.setLooping(true);
        alarmSound.start();


    }


    private void vibratePhone() {

        //https://developer.android.com/reference/android/os/VibrationEffect.html vibration help
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(30000, VibrationEffect.DEFAULT_AMPLITUDE));

    }


    private void createRadioButtons() {

        int[] numMinutes = getResources().getIntArray(R.array.preset_count_down_times);
        for (int minutes : numMinutes) {


            RadioButton button = new RadioButton(this);
            button.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            button.setText(getString(R.string.num_minutes, minutes));
            button.setTextColor(Color.WHITE);
            button.setOnClickListener(v -> {

                if (minutes == 1) {
                    startInMilliseconds = MILLISECOND_TO_MINUTE;
                } else if (minutes == 2) {
                    startInMilliseconds = 2 * MILLISECOND_TO_MINUTE;
                } else if (minutes == 3) {
                    startInMilliseconds = 3 * MILLISECOND_TO_MINUTE;
                } else if (minutes == 5) {
                    startInMilliseconds = 5 * MILLISECOND_TO_MINUTE;
                } else if (minutes == 10) {
                    startInMilliseconds = 10 * MILLISECOND_TO_MINUTE;
                }
                setTime(startInMilliseconds);

            });

            group.addView(button);
        }
    }

    BroadcastReceiver soundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            alarmSound.stop();
            vibrator.cancel();
        }
    };


    private void setTime(long milliseconds) {

        startInMilliseconds = milliseconds;
        resetTimer();

    }

    private void startTimer() {
        progressPie = findViewById(R.id.timer_progress_pie);
        progressPie.setProgress(noProgressYet);

        finishTime = System.currentTimeMillis() + timeLeftInMilliseconds;
        long duration = getDuration();

        countDownTime = new CountDownTimer(duration, MILLISECOND_TO_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeLeftInMilliseconds = millisUntilFinished;
                refreshCountDown();

                progressPie.setProgress(100 - (int)(timeLeftInMilliseconds*100/startInMilliseconds));

            }

            @Override
            public void onFinish() {
                playSound();
                vibratePhone();
                vibrator.cancel();
//                createNotification();
                Toast.makeText(TimeoutActivity.this, "Click the reset button to stop alarm", Toast.LENGTH_LONG).show();
                refreshButtonDisplay();

            }
        }.start();

        isTimerRunning = true;
        refreshButtonDisplay();
    }


    private void pauseTimer() {

        countDownTime.cancel();
        isTimerRunning = false;
        isTimerPause = true;
        refreshButtonDisplay();

    }


    private void resetTimer() {
        if (progressPie != null) {
            progressPie.setProgress(noProgressYet);
        }

        timeLeftInMilliseconds = startInMilliseconds;
        refreshCountDown();
        refreshButtonDisplay();

    }


    private void refreshCountDown() {

        int hours = (int) (timeLeftInMilliseconds / 1000) / 3600;
        int minutes = (int) ((timeLeftInMilliseconds / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMilliseconds / 1000) % 60;

        String timeLeftFormatted;

        if (hours > 0) {

            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);

        } else {

            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);

        }

        countDownText.setText(timeLeftFormatted);

    }
    private void refreshButtonDisplay() {

        if (isTimerRunning) {

            selectTimerLength.setVisibility(View.INVISIBLE);
            group.setVisibility(View.INVISIBLE);
            customizeTimerLength.setVisibility(View.INVISIBLE);
            inputText.setVisibility(View.INVISIBLE);
            syntaxOfUserInput.setVisibility(View.INVISIBLE);
            setBtn.setVisibility(View.INVISIBLE);
            resetBtn.setVisibility(View.VISIBLE);
            startPauseBtn.setText( R.string.pause);

            if ( speedStatement != null ){
                speedStatement.setVisibility(View.VISIBLE);
            }
        } else {

            selectTimerLength.setVisibility(View.VISIBLE);
            group.setVisibility(View.VISIBLE);
            customizeTimerLength.setVisibility(View.VISIBLE);
            inputText.setVisibility(View.VISIBLE);
            syntaxOfUserInput.setVisibility(View.VISIBLE);
            setBtn.setVisibility(View.VISIBLE);
            group.setVisibility(View.VISIBLE);
            resetBtn.setVisibility(View.VISIBLE);
            startPauseBtn.setText(R.string.start);

            if (speedStatement != null){
                speedStatement.setVisibility(View.INVISIBLE);
                speedStatement = null;
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = this.getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(START_TIME, startInMilliseconds);
        editor.putLong(TIME_LEFT, timeLeftInMilliseconds);
        editor.putBoolean(TIMER_RUNNING, isTimerRunning);
        editor.putLong(TIME_FINISHED, finishTime);

        editor.apply();

    }


    @Override
    protected void onStart() {

        super.onStart();

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        startInMilliseconds = prefs.getLong(START_TIME, 0);
        timeLeftInMilliseconds = prefs.getLong(TIME_LEFT, startInMilliseconds);
        isTimerRunning = prefs.getBoolean(TIMER_RUNNING, false);


        refreshCountDown();
        refreshButtonDisplay();


        if (isTimerRunning) {

            finishTime = prefs.getLong(TIME_FINISHED, 0);
            timeLeftInMilliseconds = finishTime - System.currentTimeMillis();

            if (timeLeftInMilliseconds < 0) {

                timeLeftInMilliseconds = 0;
                isTimerRunning = false;
                refreshCountDown();

            } else {

                isTimerRunning = true;
                startTimer();
                refreshButtonDisplay();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.speed_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (isTimerRunning){
            int speedID = item.getItemId();
            int[] speedsText = {25,50,75,100,200,300,400};

            ArrayList<Integer> speedIDs = new ArrayList<>();
            addSpeedIDs(speedIDs);

            for (int i = 0; i < speedIDs.size(); i++) {
                if (speedID == speedIDs.get(i)) {
                    speedStatement = findViewById(R.id.speedStatement);
                    speedStatement.setText(getString(R.string.speedStatement, speedsText[i]));

                    rate = speedsText[i];

                    chooseSpeed(i);
                    break;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSpeedIDs(ArrayList<Integer> speedIDs) {
        speedIDs.add(R.id.speed25);
        speedIDs.add(R.id.speed50);
        speedIDs.add(R.id.speed75);
        speedIDs.add(R.id.speed100);
        speedIDs.add(R.id.speed200);
        speedIDs.add(R.id.speed300);
        speedIDs.add(R.id.speed400);
    }

    private void chooseSpeed(int i){
        switch (i) {
            case 0: //25%
                countDownTime.cancel();
                startInMilliseconds = startInMilliseconds * 4;
                timeLeftInMilliseconds = startInMilliseconds;

                startTimer();
                break;
            case 1: // 50%
                countDownTime.cancel();
                startInMilliseconds = startInMilliseconds * 2;
                timeLeftInMilliseconds = startInMilliseconds;
                startTimer();

                break;
            case 2: // 75%
                countDownTime.cancel();
                startInMilliseconds = (startInMilliseconds * 5) / 4;
                timeLeftInMilliseconds = startInMilliseconds;
                startTimer();
                break;
            case 3: //100%
                break;
            case 4: //200%
                countDownTime.cancel();
                startInMilliseconds = startInMilliseconds / 2;
                timeLeftInMilliseconds = startInMilliseconds;
                startTimer();
                break;
            case 5: //300%
                countDownTime.cancel();
                startInMilliseconds = startInMilliseconds / 3;
                timeLeftInMilliseconds = startInMilliseconds;
                startTimer();
                break;
            case 6: //400%
                countDownTime.cancel();
                startInMilliseconds = startInMilliseconds / 4;
                timeLeftInMilliseconds = startInMilliseconds;
                startTimer();

                break;
        }
    }

    private long getDuration(){
        long duration;
        if (isTimerRunning){
            duration = timeLeftInMilliseconds;
        }else if(isTimerPause){
            duration = timeLeftInMilliseconds;
        }
        else {
            duration = startInMilliseconds;
        }
        return duration;
    }

}