package ca.cmpt276.parentApp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;


import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;
import ca.cmpt276.parentApp.model.TaskManager;
import ca.cmpt276.parentApp.model.TurnOnTask;

/**
 * AddTaskActivity class - add and save a task's name feature
 */
public class AddTaskActivity extends AppCompatActivity {


    private static final String NAME_PREF_NAME = "Task name";
    private static final String PREF_NAME = "AppPrefs";
    private static final String UNASSIGNED = "unassigned";

    private final GeneralManager genManager = GeneralManager.getInstance();
    private final TaskManager taskManager = genManager.getTaskManager();
    EditText taskInput;
    Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        getSupportActionBar().setTitle(R.string.title_activity_add_task);

        taskInput = findViewById(R.id.taskDescriptionInput);
        saveButton = findViewById(R.id.saveButton);

        setupSaveButton();

    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, AddTaskActivity.class);
    }


    private void setupSaveButton() {
        saveButton.setOnClickListener(view -> {

            if (taskInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(AddTaskActivity.this, getString(R.string.fill_in_blank), Toast.LENGTH_SHORT).show();
            } else {

                taskManager.addTask(taskInput.getText().toString());

                if (genManager.getChildren().getNumChildren() > 0) {
                    String firstChild = genManager.getChildren().getChildNameAt(0);

                    ArrayList<TurnOnTask> taskJustAdded = taskManager.getTaskAt(taskManager.getNumTasks()-1);
                    TurnOnTask firstTurnOnTask = taskJustAdded.get(0);
                    firstTurnOnTask.setChildName(firstChild);
                    firstTurnOnTask.setChildImageUsingChildName(firstChild);
                }

                Toast.makeText(AddTaskActivity.this, getString(R.string.task_added), Toast.LENGTH_SHORT).show();
                saveTaskName(taskInput.getText().toString());
                finish();
            }
        });
    }


    private void saveTaskName(String name) {
        SharedPreferences prefs = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NAME_PREF_NAME, name);
        editor.apply();
    }
}
