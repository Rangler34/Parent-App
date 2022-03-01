package ca.cmpt276.parentApp.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import ca.cmpt276.parentApp.R;

/**
 * CoinFlipWithoutChildrenActivity class - display an interface with buttons and coin flip options with no children involved
 */
public class CoinFlipWithoutChildrenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_without_children);

        ActionBar ab = getSupportActionBar();
        // update: getSupportActionBar(); change to ab
        ab.setTitle(R.string.title_activity_coin_flip_no_children);
        ab.setDisplayHomeAsUpEnabled(true);

        Button flipCoinButton = findViewById(R.id.flipCoinButton);
        flipCoinButton.setOnClickListener(view -> {
            Intent intent = new Intent(CoinFlipWithoutChildrenActivity.this, FlipCoinActivity.class);
            intent.putExtra("Has picker", false);
            startActivity(intent);
        });

        Button coinFlipHistoryButton = findViewById(R.id.coinFlipHistoryButton);
        coinFlipHistoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(CoinFlipWithoutChildrenActivity.this, CoinFlipHistoryActivity.class);
            startActivity(intent);
        });
    }
}