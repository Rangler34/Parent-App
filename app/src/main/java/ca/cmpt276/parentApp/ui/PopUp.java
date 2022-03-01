package ca.cmpt276.parentApp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.Children;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;
import ca.cmpt276.parentApp.model.TaskManager;
import ca.cmpt276.parentApp.model.TurnOnTask;

/**
 * Activity which displays the next child's name and photo, and the current task. Allows the user to complete
 * the task which sends the next child in line to do the task and refreshes the queue. Also allows user to
 * cancel.
 */
public class PopUp extends AppCompatActivity {


    private static final String INDEX = "Index position";
    TextView childInfo, taskInfo;
    ImageView childImg, childImgDefault;
    Button completeBtn, cancelBtn, taskHistoryBtn;

    GeneralManager genManager = GeneralManager.getInstance();
    CoinFlipManager coinFlipManager = genManager.getCoinFlipManager();
    TaskManager taskManager = genManager.getTaskManager();
    Children childrenData = genManager.getChildren();

    int position;
    String currentChildName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        childInfo = findViewById(R.id.childInfo);
        taskInfo = findViewById(R.id.taskDetails);
        childImg = findViewById(R.id.childImage);
        childImgDefault = findViewById(R.id.childImageDefault);
        completeBtn = findViewById(R.id.completeBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        taskHistoryBtn = findViewById(R.id.taskHistoryButton);

        extractIntentData();
        setUpCancelBtn();
        setUpCompleteBtn();
        setUpTaskHistoryBtn();
        displayChildAndTaskText();

    }


    private void displayChildAndTaskText() {

        TurnOnTask currentTurn = taskManager.getCurrentTurnOnTaskAt(position);
        String taskDescription = currentTurn.getTaskDescription();
        String childOnTask = currentTurn.getChildName();
        int childIndex = childrenData.getIndexOfChild(childOnTask);

        taskInfo.setText(taskDescription);
        childInfo.setText(childOnTask);

        Uri imgUri = childrenData.getChildPicAt(childIndex);
        if (imgUri == null) {
            childImg.setVisibility(View.INVISIBLE);
            childImgDefault.setVisibility(View.VISIBLE);
        }
        else {
            childImg.setImageURI(imgUri);
            childImg.setVisibility(View.VISIBLE);
            childImgDefault.setVisibility(View.INVISIBLE);
        }
    }


    public void setUpCompleteBtn() {
        completeBtn.setOnClickListener(v -> {

            taskManager.getCurrentTurnOnTaskAt(position).recordDateOfTurn();

            if (childrenData.getNumChildren() > 0) {
                assignTaskToNextChild();
            }

            finish();
        });
    }


    public void setUpTaskHistoryBtn() {
        taskHistoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PopUp.this, TaskHistoryActivity.class);
            intent.putExtra("Task Index", position);
            startActivity(intent);
        });
    }

    public void assignTaskToNextChild() {
        currentChildName = taskManager.getCurrentTurnOnTaskAt(position).getChildName();
        int indexOfChildOnTask = childrenData.getIndexOfChild(currentChildName);

        indexOfChildOnTask++;

        if (indexOfChildOnTask >= childrenData.getNumChildren()) {
            indexOfChildOnTask = 0;
        }

        String nextChildName = childrenData.getChildNameAt(indexOfChildOnTask);
        taskManager.assignTaskToNextChild(position, nextChildName);
    }

    public void setUpCancelBtn() {
        cancelBtn.setOnClickListener(v -> finish());
    }


    private void extractIntentData() {
        Intent intent = getIntent();
        position = intent.getIntExtra(INDEX, 0);
    }


    public static Intent makeIntent(Context context, int index) {
        Intent intent = new Intent(context, PopUp.class);
        intent.putExtra(INDEX, index);
        return intent;
    }
}