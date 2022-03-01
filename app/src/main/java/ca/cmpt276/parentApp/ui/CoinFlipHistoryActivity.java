package ca.cmpt276.parentApp.ui;

import androidx.appcompat.app.AppCompatActivity;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;

import android.os.Bundle;
import android.widget.ListView;

/**
 * CoinFlipHistoryActivity class - display a list of all coin flips and its details, including
 * the picker, date and time of the coin flip, and the result of the coin flip.
 */
public class CoinFlipHistoryActivity extends AppCompatActivity {

    private final int HEAD = 0;
    private CoinFlipManager coinFlipManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_history);
        getSupportActionBar().setTitle(R.string.title_activity_coin_flip_history);

        GeneralManager genManager = GeneralManager.getInstance();
        coinFlipManager = genManager.getCoinFlipManager();

        populateListView();
    }

    private void populateListView() {

        CoinFlipsAdapter adapter = new CoinFlipsAdapter(
                this, R.layout.coin_flip_history_item, coinFlipManager.getCoinFlipList());

        ListView coinFlipListView = (ListView) findViewById(R.id.listViewCoinFlip);
        coinFlipListView.setAdapter(adapter);

    }
}