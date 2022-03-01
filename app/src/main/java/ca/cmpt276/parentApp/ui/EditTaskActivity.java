package ca.cmpt276.parentApp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;
import ca.cmpt276.parentApp.model.TaskManager;


/**
 * EditChildActivity class - display edit feature for user to rename
 */
public class EditTaskActivity extends AppCompatActivity {

    private final GeneralManager genManager = GeneralManager.getInstance();
    private final TaskManager taskManager = genManager.getTaskManager();
    private String taskDescription;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        getSupportActionBar().setTitle(R.string.title_activity_edit_task);

        extractDataFromIntent();

        taskDescription = taskManager.getCurrentTurnOnTaskAt(position).getTaskDescription();

        setupEditTextForTaskDescription();
        setupSaveButton();
    }

    public static Intent makeIntent(Context context, int index) {
        Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra("Task index", index);
        return intent;
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        position = intent.getIntExtra("Task index", 0);
    }

    private void setupEditTextForTaskDescription() {
        EditText editText = findViewById(R.id.taskDescriptionInput);
        String currTaskDescription = taskDescription;
        editText.setText(currTaskDescription);
    }

    private void setupSaveButton() {
        EditText userInput = findViewById(R.id.taskDescriptionInput);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            if (userInput.getText().toString().trim().isEmpty()){
                Toast.makeText(EditTaskActivity.this, getString(R.string.fill_in_blank), Toast.LENGTH_SHORT).show();
            }else {
                taskManager.editTaskDescriptionAt(position, userInput.getText().toString());
                Toast.makeText(EditTaskActivity.this,  getString(R.string.task_updated), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}