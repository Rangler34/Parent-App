package ca.cmpt276.parentApp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;
import ca.cmpt276.parentApp.model.TaskManager;
import ca.cmpt276.parentApp.model.TurnOnTask;

//TaskHistoryActivity - display all previous turns on a single task

public class TaskHistoryActivity extends AppCompatActivity {

    private int taskIndex;
    private GeneralManager genManager = GeneralManager.getInstance();
    private TaskManager taskManager = genManager.getTaskManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        getSupportActionBar().setTitle(R.string.task_history);
        extractDataFromIntent();
        populateListView();
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        taskIndex = intent.getIntExtra("Task Index", 0);
    }

    private void populateListView() {

        TaskHistoryAdapter adapter = new TaskHistoryAdapter();

        ListView taskHistoryListView = (ListView) findViewById(R.id.listViewTaskHistory);
        taskHistoryListView.setAdapter(adapter);

    }

    private class TaskHistoryAdapter extends ArrayAdapter<TurnOnTask> {
        public TaskHistoryAdapter(){
            super(TaskHistoryActivity.this, R.layout.task_history_item, taskManager.getTaskAt(taskIndex));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.task_history_item, parent, false); //xml file get become object you got a view
            }

            TextView textViewDate = itemView.findViewById(R.id.textViewDate);
            TextView textViewChildName = itemView.findViewById(R.id.textViewChildName);
            ImageView imageViewChildPic = itemView.findViewById(R.id.imageViewChildPic);

            TurnOnTask currTurn = taskManager.getTaskAt(taskIndex).get(position);

            textViewDate.setText(currTurn.getDateOfTurn());
            textViewChildName.setText(currTurn.getChildName());
            if (currTurn.getChildImageID() != null) {
                imageViewChildPic.setImageURI(currTurn.getChildImageID());
            } else {
                imageViewChildPic.setImageResource(R.drawable.default_photo);
            }

            return itemView;
        }
    }

}