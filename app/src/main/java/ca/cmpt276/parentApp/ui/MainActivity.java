package ca.cmpt276.parentApp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;

/**
 * MainActivity class - main interface of the app, display major features' with buttons
 */
public class MainActivity extends AppCompatActivity {

    private GeneralManager genManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        genManager = GeneralManager.getInstance();

        Button coinFlipBtn = findViewById(R.id.coinFlipBtn);
        coinFlipBtn.setOnClickListener(view -> {
            if (genManager.getChildren().getNumChildren()  == 0) {
                Intent intent = new Intent(MainActivity.this, CoinFlipWithoutChildrenActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(MainActivity.this, SetUpCoinFlipActivity.class);
                startActivity(intent);
            }
        });

        Button childBtn = findViewById(R.id.childBtn);
        childBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChildInfoActivity.class);
            startActivity(intent);
        });

        Button timeoutBtn = findViewById(R.id.timeoutBtn);
        timeoutBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TimeoutActivity.class);
            startActivity(intent);
        });

        Button taskBtn = findViewById(R.id.taskBtn);
        taskBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TaskInfoActivity.class);
            startActivity(intent);
        });

        Button help = findViewById(R.id.helpBtn);
        help.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        Button breathBtn = findViewById(R.id.breathBtn);
        breathBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BreathActivity.class);
            startActivity(intent);
        });

    }
}