package ca.cmpt276.parentApp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;

/**
 * ChangePickerActivity class - display a queue of the configuration children in the order
 * of first to last child who gets to be the picker of the following coin flip.
 * User can manually select a new picker from the list.
 */
public class ChangePickerActivity extends AppCompatActivity {

    private final GeneralManager genManager = GeneralManager.getInstance();
    private final CoinFlipManager coinFlipManager = genManager.getCoinFlipManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_picker);
        getSupportActionBar().setTitle(R.string.title_activity_change_picker);

        populateListView();
        registerClickCallback();
    }

    private void populateListView() {

        ArrayAdapter<String> adapter = new PickerListAdapter();

        ListView childrenList = findViewById(R.id.pickerQueueListView);
        childrenList.setAdapter(adapter);
    }

    private class PickerListAdapter extends ArrayAdapter<String> {
        public PickerListAdapter() {
            super(ChangePickerActivity.this, R.layout.child, coinFlipManager.getPickerQueue().getChildrenNameList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View childView = convertView;
            if (childView == null) {
                childView = getLayoutInflater().inflate(R.layout.child, parent, false);
            }

            String childName = coinFlipManager.getPickerQueue().getChildNameAt(position);

            TextView textViewChildName = childView.findViewById(R.id.textViewChildName);
            textViewChildName.setText(childName);

            int childIndex = genManager.getChildren().getIndexOfChild(childName);
            Uri childImageID = genManager.getChildren().getChildPicAt(childIndex);
            ImageView imageViewChild = childView.findViewById(R.id.imageViewChildPic);
            if (childImageID != null) {
                imageViewChild.setImageURI(childImageID);
            } else {
                imageViewChild.setImageResource(R.drawable.default_photo);
            }
            return childView;
        }
    }

    private void registerClickCallback() {
        ListView pickerTurnQueue = (ListView) findViewById(R.id.pickerQueueListView);
        pickerTurnQueue.setOnItemClickListener((parent, viewClicked, position, id) -> {
            String child = coinFlipManager.getPickerQueue().getChildNameAt(position);
            coinFlipManager.setCurrentPicker(child);

            Toast.makeText(
                    ChangePickerActivity.this,
                    (getString(R.string.new_picker)).concat(child),
                    Toast.LENGTH_LONG).show();
            finish();
        });
    }
}