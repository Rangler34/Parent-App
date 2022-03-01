package ca.cmpt276.parentApp.ui;

import androidx.appcompat.app.AppCompatActivity;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * SetUpCoinFlipActivity class - display an interface with the current picker's name, buttons that
 * allow the picker to choose heads or tails, options to change the picker, and a button to view
 * coin flip history.
 */

public class SetUpCoinFlipActivity extends AppCompatActivity {

    private final GeneralManager genManager = GeneralManager.getInstance();
    private final CoinFlipManager coinFlipManager = genManager.getCoinFlipManager();

    private final int HEADS = 0;
    private final int TAILS = 1;
    private int pick; //heads = 0, tails = 1

    private TextView textViewPickerName;
    private ImageView imageViewPicker;
    private String initialPicker = coinFlipManager.getCurrentPicker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_coin_flip);
        getSupportActionBar().setTitle(R.string.title_activity_set_up_coin_flip);

        textViewPickerName = findViewById(R.id.textViewPickerName);
        imageViewPicker = findViewById(R.id.imageViewPicker);

        displayPicker();

        RadioButton headButton = findViewById(R.id.radioButtonHead);
        headButton.setOnClickListener(view -> pick = HEADS);

        RadioButton tailButton = findViewById(R.id.radioButtonTail);
        tailButton.setOnClickListener(view -> pick = TAILS);

        Button flipCoinButton = findViewById(R.id.flipCoinButton);
        flipCoinButton.setOnClickListener(view -> {
            Intent intent = new Intent(SetUpCoinFlipActivity.this, FlipCoinActivity.class);
            intent.putExtra("Has picker", true);
            intent.putExtra(getString(R.string.side_picked), pick);
            startActivity(intent);
        });

        Button coinFlipHistoryButton = findViewById(R.id.coinFlipHistoryButton);
        coinFlipHistoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(SetUpCoinFlipActivity.this, CoinFlipHistoryActivity.class);
            startActivity(intent);
        });

        Button changePickerButton = findViewById(R.id.changePickerButton);
        changePickerButton.setOnClickListener(view -> {
            Intent intent = new Intent(SetUpCoinFlipActivity.this, ChangePickerActivity.class);
            startActivity(intent);
        });

        Button flipCoinWithoutPickerButton = findViewById(R.id.flipCoinWithoutPickerButton);
        flipCoinWithoutPickerButton.setOnClickListener(view -> {
            Intent intent = new Intent(SetUpCoinFlipActivity.this, FlipCoinActivity.class);
            intent.putExtra("Has picker", false);
            startActivity(intent);
        });
    }

    protected void onResume() {
        super.onResume();
        displayPicker();
    }

    public void onBackPressed() {
        super.onBackPressed();
        coinFlipManager.setCurrentPicker(initialPicker);
    }

    private void displayPicker() {
        String pickerName = coinFlipManager.getCurrentPicker();
        textViewPickerName.setText(pickerName.concat(getString(R.string.comma)));
        ImageView childPhoto = findViewById(R.id.imageViewPicker);
        ImageView defaultPhoto = findViewById(R.id.imageViewPickerDefaultPhoto);

        int index = genManager.getChildren().getIndexOfChild(pickerName);
        Uri pickerImageID = genManager.getChildren().getChildPicAt(index);
        if (pickerImageID == null) {
            defaultPhoto.setVisibility(View.VISIBLE);
            childPhoto.setVisibility(View.INVISIBLE);

        }
        else {
            imageViewPicker.setImageURI(pickerImageID);
            defaultPhoto.setVisibility(View.INVISIBLE);
            childPhoto.setVisibility(View.VISIBLE);
        }
    }
}