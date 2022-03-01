package ca.cmpt276.parentApp.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.navigation.ui.AppBarConfiguration;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.databinding.ActivityChildInfoBinding;
import ca.cmpt276.parentApp.model.Children;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;

/**
 * ChildInfoActivity class - display a list with children's names with delete feature
 */
public class ChildInfoActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityChildInfoBinding binding;

    private final GeneralManager genManager = GeneralManager.getInstance();
    private final Children childrenData = genManager.getChildren();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChildInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        populateListView();
        setupAddButton();
    }

    //Display updated name list
    @Override
    protected void onStart(){
        super.onStart();
        populateListView();
    }

    private void setupAddButton() {
        FloatingActionButton addButton = findViewById(R.id.addNameButton);
        addButton.setOnClickListener(view -> {
            //Launch add child activity
            Intent intent = AddChildActivity.makeIntent(ChildInfoActivity.this);
            startActivity(intent);
        });
    }

    private void populateListView() {
        if (childrenData.getNumChildren() >= 0) {
            disableNoChildText();
            //Build Adapter
            ArrayAdapter<String> adapter = new ChildrenListAdapter();
            ListView nameList = findViewById(R.id.childList);
            nameList.setAdapter(adapter);
        }
        if (childrenData.getNumChildren() == 0) {
            noChildText();
        }
    }

    //inner class for ListView ArrayAdapter
    private class ChildrenListAdapter extends ArrayAdapter<String> {
        public ChildrenListAdapter() {
            super(ChildInfoActivity.this,
                    R.layout.button_and_item,
                    childrenData.getChildrenNameList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.button_and_item,
                        parent,
                        false); //xml file get become object you got a view
            }

            //Find the name to work with
            String currentChild = childrenData.getChildNameAt(position);
            TextView textView = itemView.findViewById(R.id.next_child);
            textView.setOnClickListener(view -> {
                //Launch edit child activity
                Intent intent = EditChildActivity.makeIntent(ChildInfoActivity.this,
                        currentChild,
                        position);
                startActivity(intent);
            });
            //set name Text View:
            textView.setText(childrenData.getChildNameAt(position));

            ImageView imageViewDefault = itemView.findViewById(R.id.profile_default_pic);
            if (childrenData.getChildPicAt(position) == null) {
                imageViewDefault.setVisibility(View.VISIBLE);
            }
            //set the default Image View

            ImageView imageView = itemView.findViewById(R.id.profile_pic);
            imageView.setOnClickListener(view -> {
                //Launch edit child activity
                Intent intent = EditChildActivity.makeIntent(ChildInfoActivity.this,
                        currentChild,
                        position);
                startActivity(intent);
            });
            //set picture Image View:
            if (childrenData.getChildPicAt(position) != null) {
                imageView.setImageURI(childrenData.getChildPicAt(position));
            }

            //Fill the delete button
            ImageButton removeButton = itemView.findViewById(R.id.item_removeButton);
            removeButton.setImageResource(R.drawable.ic_remove);
            removeButton.setOnClickListener(view -> {
                childrenData.removeChild(position);
                genManager.getCoinFlipManager().removeChildFromPickerQueue(position);
                genManager.getTaskManager().removeChildFromAllAssignedTasks(currentChild);
                populateListView();
            });

            return itemView;
        }
    }//inner class ends

    private void disableNoChildText() {
        TextView tv = findViewById(R.id.noChildReminder);
        tv.setText(getString(R.string.disableNoChildReminder));
    }

    private void noChildText() {
        TextView tv = findViewById(R.id.noChildReminder);
        tv.setText(getString(R.string.noChildReminder));
    }
}

