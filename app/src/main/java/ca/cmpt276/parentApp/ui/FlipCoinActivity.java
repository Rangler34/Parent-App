package ca.cmpt276.parentApp.ui;

import static android.media.MediaPlayer.create;
import static ca.cmpt276.parentApp.R.string.heads;
import static ca.cmpt276.parentApp.R.string.side_picked;
import static ca.cmpt276.parentApp.R.string.tails;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.Children;
import ca.cmpt276.parentApp.model.CoinFlip;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;

/**
 * FlipCoinActivity class - display an interface with buttons and coin flip options with when a child's turn
 */
public class FlipCoinActivity extends AppCompatActivity {

    private GeneralManager genManager = GeneralManager.getInstance();
    private CoinFlipManager coinFlipManager = genManager.getCoinFlipManager();
    private CoinFlip coinFlip = new CoinFlip();

    private ImageView coin;
    LottieAnimationView flipCoinAnimation;
    private TextView coinSideText;

    private final int HEAD = 0;
    private final int TAIL = 1;


    private static final String DATE_TIME_PREF_NAME = "Date and time";
    private static final String PICKER_NAME_PREF_NAME = "Picker name";
    private static final String PICKER_IMAGE_ID_PREF_NAME = "Picker Image ID";
    private static final String COIN_RESULT_PREF_NAME = "Coin result";
    private static final String IS_WON_PREF_NAME = "Is won";
    private static final String PREF_NAME = "AppPrefs";

    private static int timeOut = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);
        getSupportActionBar().setTitle(R.string.title_activity_flip_coin);

        coin = (ImageView) findViewById(R.id.coin);
        flipCoinAnimation = findViewById(R.id.flip_a_coin_animation);
        coinSideText = findViewById(R.id.coin_side);

        Children childrenData = genManager.getChildren();

        if (childrenData.getNumChildren() != 0) {
            Intent intent = getIntent();

            if (intent.getBooleanExtra("Has picker", false)) {
                String pickerName = coinFlipManager.getCurrentPicker();
                int sidePicked = intent.getIntExtra(getString(R.string.side_picked), 1);
                int index = childrenData.getIndexOfChild(pickerName);
                Uri pickerImageID = childrenData.getChildPicAt(index);
                coinFlip = new CoinFlip(pickerName, pickerImageID, sidePicked);
            } else {
                coinFlip = new CoinFlip();
            }
        } else {
            coinFlip = new CoinFlip();
        }

        coinFlipManager.addCoinFlip(coinFlip);
        saveCoinFlip(coinFlip);

        onFlipCoin();


    }

    private void saveCoinFlip(CoinFlip coinFlip) {

        SharedPreferences prefsDateTime = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editorDateTime = prefsDateTime.edit();
        editorDateTime.putString(DATE_TIME_PREF_NAME, coinFlip.getDateTime());
        editorDateTime.apply();

        SharedPreferences prefsPickerName = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editorPickerName = prefsPickerName.edit();
        editorPickerName.putString(PICKER_NAME_PREF_NAME, coinFlip.getPickerName());
        editorPickerName.apply();

        SharedPreferences prefsPickerImageID = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editorPickerImageID = prefsPickerImageID.edit();
        if (coinFlip.getPickerImageID() == null) {
            editorPickerImageID.putString(PICKER_IMAGE_ID_PREF_NAME, null);
            editorPickerImageID.apply();
        } else {
            editorPickerImageID.putString(PICKER_IMAGE_ID_PREF_NAME, coinFlip.getPickerImageID().toString());
            editorPickerImageID.apply();
        }

        SharedPreferences prefsCoinResult = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editorCoinResult = prefsCoinResult.edit();
        editorCoinResult.putInt(COIN_RESULT_PREF_NAME, coinFlip.getCoinResult());
        editorCoinResult.apply();

        SharedPreferences prefsIsWon = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editorIsWon = prefsIsWon.edit();
        editorIsWon.putBoolean(IS_WON_PREF_NAME, coinFlip.isWon());
        editorIsWon.apply();
    }

    private void onFlipCoin() {

        flipCoin();

        TimerTask task = new TimerTask(){
            public void run(){

                int resultSide = coinFlip.getCoinResult();
                boolean isWon = coinFlip.isWon();

                if (resultSide == HEAD) {
                    showFlipResult(R.drawable.head, getString(R.string.heads), isWon);
                } else {
                    showFlipResult(R.drawable.tail, getString(R.string.tails), isWon);
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, timeOut);

    }

    private void flipCoin() {

        final MediaPlayer mediaPlayer = create(this, R.raw.coin_flip);
        mediaPlayer.start();

        int duration = 4000;
        int end = 1000;

        flipCoinAnimation.animate()
                .translationX(1600)
                .setDuration(duration)
                .setStartDelay(end)
                .getStartDelay();
    }

    private void showFlipResult(int imageId, String coinSide, boolean isWon) {

        playMedia(isWon);

        int duration = 1000;

        Animation coinFadeOut = new AlphaAnimation(1, 0);
        coinFadeOut.setInterpolator(new AccelerateInterpolator());
        coinFadeOut.setDuration(duration);
        coinFadeOut.setFillAfter(true);
        coin.startAnimation(coinFadeOut);

        coinFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation coinAnimation) {

            }

            @Override
            public void onAnimationEnd(Animation coinAnimation) {

                coin.setImageResource(R.drawable.head);
                coin.setVisibility(View.VISIBLE);

                coin.setImageResource(imageId);
                coinSideText.setText(coinSide);

                Animation coinFadeIn = new AlphaAnimation(0, 1);
                coinFadeIn.setInterpolator(new DecelerateInterpolator());
                coinFadeIn.setDuration(duration);
                coinFadeIn.setFillAfter(true);

                coin.startAnimation(coinFadeIn);

            }

            @Override
            public void onAnimationRepeat(Animation coinAnimation) {

            }
        });
    }

    private void playMedia(boolean isWon) {

        if (isWon) { //sidePicked
            MediaPlayer mediaPlayerWinning = create(this, R.raw.winning);
            mediaPlayerWinning.start();
        } else {
            MediaPlayer mediaPlayerLosing = create(this, R.raw.losing);
            mediaPlayerLosing.start();
        }
    }
}

/* Coin flip json file by https://lottiefiles.com/ */