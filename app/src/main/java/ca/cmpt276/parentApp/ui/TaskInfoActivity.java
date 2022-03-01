package ca.cmpt276.parentApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;
import ca.cmpt276.parentApp.model.TaskManager;
import ca.cmpt276.parentApp.model.TurnOnTask;

//Icon source: www.flaticon.com

/**
 * TaskInfoActivity - display task list on screen
 */
public class TaskInfoActivity extends AppCompatActivity {


    private static final GeneralManager genManager = GeneralManager.getInstance();
    private final TaskManager taskManager = genManager.getTaskManager();
    private static final String UNASSIGNED = "unassigned";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        getSupportActionBar().setTitle(R.string.title_activity_task_info);

        setupAddButton();
        populateListView();
    }


    //Display updated task list
    @Override
    protected void onStart() {
        super.onStart();
        populateListView();
    }


    private void setupAddButton() {
        FloatingActionButton addButton = findViewById(R.id.addTaskButton);

        addButton.setOnClickListener(view -> {
            //Launch add task activity
            Intent intent = AddTaskActivity.makeIntent(TaskInfoActivity.this);
            startActivity(intent);
        });
    }

    private void populateListView(){
        if (taskManager.getNumTasks() >= 0) {
            ArrayAdapter<ArrayList<TurnOnTask>> adapter = new TasksListAdapter();
            ListView taskList = findViewById(R.id.taskList);
            taskList.setAdapter(adapter);
        }
    }

    //inner class for ListView ArrayAdapter
    private class TasksListAdapter extends ArrayAdapter<ArrayList<TurnOnTask>> {
        public TasksListAdapter(){
              super(TaskInfoActivity.this, R.layout.task_child_item, taskManager.getTaskList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.task_child_item, parent, false); //xml file get become object you got a view
            }

            TextView taskTextView = itemView.findViewById(R.id.task_item);

            String taskDescription = taskManager.getCurrentTurnOnTaskAt(position).getTaskDescription();
            taskTextView.setText(taskDescription);


            //pop up dialog
            taskTextView.setOnClickListener(view -> {

                if (genManager.getChildren().getChildrenNameList().size() > 0) {
                    populateListView();
                    Intent intent = PopUp.makeIntent(TaskInfoActivity.this, position);
                    startActivity(intent);

                } else {
                    Toast.makeText(TaskInfoActivity.this, getString(R.string.no_child), Toast.LENGTH_SHORT).show();
                }

            });

            //edit task
            ImageButton editButton = itemView.findViewById(R.id.item_editButton);
            editButton.setImageResource(R.drawable.ic_edit);

            editButton.setOnClickListener(view -> {

                Intent intent = EditTaskActivity.makeIntent(TaskInfoActivity.this, position);
                startActivity(intent);
            });

            //remove task
            ImageButton removeButton = itemView.findViewById(R.id.item_removeButton);
            removeButton.setImageResource(R.drawable.ic_remove);

            removeButton.setOnClickListener(view -> {
                taskManager.removeTask(position);
                populateListView();//refresh list
            });

            TextView tv = itemView.findViewById(R.id.turn_item);
            //next turn's name
            if (genManager.getChildren().getNumChildren() == 0) {
                tv.setText(getString(R.string.na));
            } else {

                TurnOnTask currTurn = taskManager.getCurrentTurnOnTaskAt(position);
                String nextChildName = currTurn.getChildName();
                if (nextChildName.equals(UNASSIGNED)) {
                    tv.setText("");
                } else {
                    tv.setText(nextChildName);
                }
            }
            return itemView;
        }
    }


}